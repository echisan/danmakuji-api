package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.MessageConstants;
import cc.dmji.api.constants.ReplyConstants;
import cc.dmji.api.entity.*;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.enums.ReplyOrderBy;
import cc.dmji.api.enums.Role;
import cc.dmji.api.enums.Status;
import cc.dmji.api.service.*;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.JwtTokenUtils;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.utils.ReplyPageInfo;
import cc.dmji.api.web.model.Replies;
import cc.dmji.api.web.model.ReplyInfo;
import cc.dmji.api.web.model.ReplyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by echisan on 2018/5/25
 */
@RestController
@RequestMapping("/replies")
public class ReplyController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ReplyController.class);
    private final int pageSize = 20;
    private final int sonPageSize = 10;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private LikeRecordService likeRecordService;

    @Autowired
    private EpisodeService episodeService;

    @Autowired
    private BangumiService bangumiService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @GetMapping
    @UserLog("获取回复")
    public ResponseEntity<Result> listEpReplies(@RequestParam(required = false) Long epId,
                                                @RequestParam(name = "rid", required = false) Long replyId,
                                                @RequestParam(value = "pn", defaultValue = "1", required = false) Integer pn,
                                                @RequestParam(value = "ps", defaultValue = "20", required = false) Integer ps,
                                                HttpServletRequest request) {

        Long userId = getUidFromRequest(request);
        Map<String, Object> data = null;
        if (epId != null) {
            data = replyService.listEpisodeRepliesToMap(userId, ReplyOrderBy.CREATE_TIME, epId, pn, ps);
//            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_BLANK, "epId不能为空"));
            if (pn == 1){
                // 获取热门评论
                List<Replies> repliesList = replyService.listEpisodeReplies(epId, ReplyOrderBy.LIKE, userId, pn, 3);
                data.put("hot",repliesList);
            }
        }
        if (replyId != null) {
            Reply reply = replyService.getReplyById(replyId);
            if (reply == null) {
                return getResponseEntity(HttpStatus.NOT_FOUND, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND));
            }

            // 如果是父级评论的话
            if (reply.getIsParent().equals(ReplyConstants.IS_PARENT)) {
                data = replyService.listEpisodeRepliesToMap(userId, ReplyOrderBy.CREATE_TIME, reply.getEpId(), findParentReplyPage(reply), ps);
            } else {

                // 如果不是父集评论就把他父集评论所在的页面查出来
                Long parentId = reply.getParentId();
                Reply parentReply = replyService.getReplyById(parentId);
                // 当前评论的父集评论所在的页数
                data = replyService.listEpisodeRepliesToMap(userId, ReplyOrderBy.CREATE_TIME, parentReply.getEpId(), findParentReplyPage(parentReply), ps);

                // 找出子评论的页码
                Long sonCount = replyService.countSonRepliesByParentId(parentId);
                // 子评论总数
                PageInfo subPageInfo = new PageInfo();

                if (sonCount > 10) {
                    Reply firstReply = replyService.getFirstSonReplyByParentId(parentId);
                    Long createTimeBetween = replyService.countByParentIdAndCreateTimeBetween(parentId, firstReply.getCreateTime(), reply.getCreateTime());

                    Integer sonPage = Math.toIntExact(((createTimeBetween + sonPageSize) / sonPageSize));
                    if (sonPage > 1) {
                        List<ReplyInfo> sonReplyList = replyService.listSonRepliesByParentId(parentId, userId, sonPage, sonPageSize);

                        List<Long> replyIds = new ArrayList<>();
                        sonReplyList.forEach(replyInfo -> replyIds.add(replyInfo.getReply().getReplyId()));
                        if (userId != null) {
                            List<LikeRecord> likeRecords = likeRecordService.listByReplyIdsAndUserId(replyIds, userId);
                            Map<Long, Byte> isLikeMap = new HashMap<>();
                            likeRecords.forEach(likeRecord -> isLikeMap.put(likeRecord.getReplyId(), likeRecord.isLike()?(byte)1:(byte)0));
                            sonReplyList.forEach(replyInfo -> {
                                if (isLikeMap.containsKey(replyInfo.getReply().getReplyId())) {
                                    replyInfo.setLikeStatus((byte) 1);
                                }
                            });
                        }

                        List<Replies> replies = (List<Replies>) data.get("replies");
                        for (Replies r : replies) {
                            if (r.getReply().getReply().getReplyId().equals(parentId)) {
                                r.setReplies(sonReplyList);
                                ReplyInfo replyInfo = r.getReply();
                                replyInfo.setIsTarget(1);
                                replyInfo.setCurPage(sonPage);
                                r.setReply(replyInfo);
                                break;
                            }
                        }
                        data.put("replies", replies);

                    }

                    subPageInfo.setTotalSize(sonCount);
                    subPageInfo.setPageNumber(sonPage);
                    subPageInfo.setPageSize(10);
                    data.put("sub_page", subPageInfo);

                } else {
                    List<Replies> replies = (List<Replies>) data.get("replies");
                    for (Replies r : replies) {
                        if (r.getReply().getReply().getReplyId().equals(parentId)) {
                            ReplyInfo replyInfo = r.getReply();
                            replyInfo.setIsTarget(1);
                            r.setReply(replyInfo);
                            break;
                        }
                    }
                    data.put("replies", replies);
                }
            }
            List<Replies> repliesList = replyService.listEpisodeReplies(epId, ReplyOrderBy.LIKE, userId, 1, 3);
            data.put("hot",repliesList);
        }
        return getResponseEntity(HttpStatus.OK, getSuccessResult(data));
    }

    @PostMapping
    @UserLog("添加回复")
    public ResponseEntity<Result> addReply(@RequestBody ReplyRequest replyRequest, HttpServletRequest request) {

        // 数据校验
        Long userId = getUidFromRequest(request);
        String nick = getNickFormRequest(request);

        if (StringUtils.isEmpty(replyRequest.getContent())) {
            return getResponseEntity(
                    HttpStatus.BAD_REQUEST,
                    getErrorResult(ResultCode.PARAM_IS_INVALID, "评论/回复不能为空")
            );

        }

        if (StringUtils.isEmpty(replyRequest.getUid())) {
            return getResponseEntity(
                    HttpStatus.BAD_REQUEST,
                    getErrorResult(ResultCode.PARAM_IS_INVALID, "用户id不能为空!!!!!!")
            );
        }

        if (replyRequest.getIs_parent().equals(ReplyConstants.NOT_PARENT)) {
            if (StringUtils.isEmpty(replyRequest.getP_rid())) {
                return getResponseEntity(
                        HttpStatus.BAD_REQUEST,
                        getErrorResult(ResultCode.PARAM_IS_INVALID, "父评论p_rid不能为空!!!!!!")
                );
            }
        }


        Reply reply = new Reply();
        Long targetUserId = null;

//        String content = replyRequest.getContent().replaceAll("\n","<br/>");
//        content = GeneralUtils.htmlEncode(content);
        String content = replyRequest.getContent();

        // 设置该评论的用户
        reply.setUserId(replyRequest.getUid());
        // 设置评论所在的集数
        reply.setEpId(replyRequest.getEp_id());
        // 设置评论内容
        reply.setContent(content);
        // 设置评论状态
        reply.setrStatus(Status.NORMAL.name());
        // 设置评论所在的页数
        reply.setrPage(replyRequest.getR_page());

        // 判断是否是父级评论，如果是父级评论, 则没有parentId
        if (replyRequest.getIs_parent().equals(ReplyConstants.IS_PARENT)) {
            reply.setIsParent(ReplyConstants.IS_PARENT);
            reply.setParentId(null);
        } else {
            // 如果是子评论的话就查一下targetUser
            if (!replyRequest.getP_uid().equals(replyRequest.getUid())) {
                targetUserId = replyRequest.getP_uid();
            }
            // 将回复设置成不是父级评论
            reply.setIsParent(ReplyConstants.NOT_PARENT);
            // 设置该回复的父级评论id
            reply.setParentId(replyRequest.getP_rid());

        }
        // 初始化评论的赞踩
        reply.setrLike(0);
        reply.setrHate(0);

        Long floor = replyService.countParentReplyByEpId(replyRequest.getEp_id());
        reply.setFloor(floor + 1);
        Reply newReply = replyService.insertReply(reply);
        logger.info("new ReplyV2:{}", newReply.toString());

        ReplyInfo replyInfo = replyService.getReplyInfoById(newReply.getReplyId());

        // 获取at列表
        List<String> nickList = DmjiUtils.findAtUsername(replyRequest.getContent());

        if (newReply.getIsParent().equals(ReplyConstants.IS_PARENT)) {
            Replies replies = new Replies();
            replies.setReplies(new ArrayList<>());
            replies.setReply(replyInfo);
            if (nickList.size() == 0) {
                return getResponseEntity(HttpStatus.OK, getSuccessResult(replies));
            } else {
                sendMessage(newReply, nick, targetUserId, nickList);
                return getResponseEntity(HttpStatus.OK, getSuccessResult(replies));
            }

        }

        sendMessage(newReply, nick, targetUserId, nickList);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(replyInfo));
    }

    @DeleteMapping("/{replyId}")
    @UserLog("删除回复")
    public ResponseEntity<Result> deleteReplyById(@PathVariable Long replyId, HttpServletRequest request) {
        Reply reply = replyService.getReplyById(replyId);
        Long replyUserId = reply.getUserId();
        Long uid = getUidFromRequest(request);
        String token = getToken(request);
        if (uid == null) {
            return getResponseEntity(HttpStatus.FORBIDDEN, getErrorResult(ResultCode.PERMISSION_DENY, "未登录，请先登录"));
        }

        String role = jwtTokenUtils.getPayload(token).getRole();
        // 如果是管理员的话就能删
        if (role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER")){
            Reply reply1 = replyService.deleteReply(reply);

            sendDeleteReplyMessage(reply1);
            return getResponseEntity(HttpStatus.OK, getSuccessResult("删除成功"));
        }
        if (!uid.equals(replyUserId)) {
            return getResponseEntity(HttpStatus.FORBIDDEN, getErrorResult(ResultCode.PERMISSION_DENY, "无权删除别人的评论呢，别搞事情"));
        }

        Reply reply1 = replyService.deleteReply(reply);
        logger.debug("删除的评论是:{}", reply1.toString());
        return getResponseEntity(HttpStatus.OK, getSuccessResult("删除成功"));
    }

    @GetMapping("/son")
    public ResponseEntity<Result> listSonReplies(@RequestParam("prid") Long prid,
                                                 @RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                                 HttpServletRequest request) {

        if (StringUtils.isEmpty(prid)) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "p_rid不能为空"));
        }

        Long userId = getUidFromRequest(request);
        Map<String, Object> data = replyService.listPageSonRepliesByParentId(prid, userId, pn, ReplyPageInfo.DEFAULT_SON_PAGE_SIZE);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(data));
    }

    /**
     * 点赞功能
     */
    @PostMapping("/like/{replyId}/{action}")
    @UserLog("点赞")
    public ResponseEntity<Result> doActionAtReply(@PathVariable("replyId") Long replyId,
                                                  @PathVariable("action") Integer action,
                                                  HttpServletRequest request) {

        if (replyId == null) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "评论id不能为空"));
        }

        Reply reply = replyService.getReplyById(replyId);
        if (reply == null) {
            return getResponseEntity(HttpStatus.NOT_FOUND, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND, "找不到该评论，操作失败"));
        }

        if (action == null) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "action不能为空，要干嘛你说清楚"));
        }

        Long userId = getUidFromRequest(request);
        if (userId == null) {
            return getResponseEntity(HttpStatus.FORBIDDEN, getErrorResult(ResultCode.PERMISSION_DENY, "先登录再点赞吧"));
        }

        // 自赞处理
        if (userId.equals(reply.getUserId())) {
            return getResponseEntity(HttpStatus.OK, getSuccessResult());
        }

        LikeRecord likeRecord;
        Reply updateReply;
        boolean requireSendMessage = true;
        // 取消点赞
        if (action == 0) {
            requireSendMessage = false;
            LikeRecord record = likeRecordService.getByReplyIdAndUserId(replyId, userId);
            if (record == null) {
                return getResponseEntity(HttpStatus.OK, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND, "根本就没点过赞呢，别取消了"));
            }

            if (!record.isLike()) {
                return getResponseEntity(HttpStatus.OK, getSuccessResult("赞还没点，自然不能取消点赞了"));
            }
            // 如果有该数据, 0则取消
            record.setLike(false);
            record.setModifyTime(new Date());
            likeRecord = likeRecordService.updateLikeRecord(record);

            reply.setrLike(reply.getrLike() - 1);
            updateReply = replyService.updateReply(reply);

            // 点赞
        } else if (action == 1) {
            LikeRecord record = likeRecordService.getByReplyIdAndUserId(replyId, userId);
            if (record == null) {
                record = new LikeRecord();
                record.setLike(true);
                record.setReplyId(replyId);
                record.setUserId(userId);
                likeRecord = likeRecordService.insertLikeRecord(record);
            } else {
                requireSendMessage = false;
                // 已经点过赞了
                if (record.isLike()) {
                    return getResponseEntity(HttpStatus.OK, getSuccessResult("已经点过赞了，就不给评论继续加了"));
                }
                record.setLike(true);
                likeRecord = likeRecordService.updateLikeRecord(record);
            }
            reply.setrLike(reply.getrLike() + 1);
            updateReply = replyService.updateReply(reply);

        } else {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "别发些奇奇怪怪的参数，只接受0或1"));
        }

        logger.debug("like record:{}", likeRecord);

        if (requireSendMessage) {
            // 发送消息
            Message message = new Message();
            message.setType(MessageType.LIKE.name());
            message.setReply(updateReply);
            message.setUserId(reply.getUserId());
            message.setContent(DmjiUtils.formatReplyContent(reply.getContent()));
            message.setmStatus(Status.NORMAL.name());
            message.setEpId(reply.getEpId());
            message.setIsRead(MessageConstants.NOT_READ);
            message.setPublisherUserId(userId);
            // 异步发送通知
            sendMessage(message);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("reply_id", updateReply.getReplyId());
        map.put("r_like", updateReply.getrLike());
        return getResponseEntity(HttpStatus.OK, getSuccessResult(map));
    }


    /**
     * 找出父集评论的页数
     */
    private Integer findParentReplyPage(Reply currentReply) {
        Reply latestReply = replyService.getLatestReplyByEpId(currentReply.getEpId());
        // 计算当前评论与最新评论之间的个数
        Long curToLatestCount =
                replyService.countByEpIdAndCreateTimeBetween(currentReply.getEpId(), currentReply.getCreateTime(), latestReply.getCreateTime());

        if (curToLatestCount == 20) {
            return 1;
        }
        int page = Math.toIntExact((curToLatestCount / pageSize) + 1);
        logger.debug("findParentReplyPage() page:" + page);
        return page;
    }

    @Async
    public void sendMessage(Message message) {
        messageService.insertMessage(message);
        logger.debug("send messages : {}", message.toString());
    }


    @Async
    public void sendMessage(Reply newReply, String nick, Long targetUserId, List<String> nickList) {

        // 初始化完毕
        // 移除发评论的人的名字
        nickList.remove(nick);
        if (targetUserId != null) {
            User user = userService.getUserById(targetUserId);
            nickList.remove(user.getNick());
        }

        // 待发送的消息列表
        List<Message> messages = new ArrayList<>();
        // 是否有at
        if (nickList.size() != 0) {
            Message message = createMessage(newReply);
            List<User> users = userService.listUserByNickIn(nickList);
            message.setType(MessageType.AT.name());
            users.forEach(user -> {
                message.setUserId(user.getUserId());
                messages.add(message);
            });
        }

        // messages 已存入需要at的用户
        // 判断该回复是否是子评论
        // 如果不是父级评论的话
        if (targetUserId != null) {
            Message message = createMessage(newReply);
            message.setUserId(targetUserId);
            message.setType(MessageType.REPLY.name());
            messages.add(message);
        }
        logger.debug("send messages {}", messages.toString());
        messageService.insertMessageList(messages);
    }

    private Message createMessage(Reply newReply) {
        // 发送通知
        // 初始化通知
        Message message = new Message();
        // 设置该评论所在的集数
        message.setEpId(newReply.getEpId());
        // 没读
        message.setIsRead(MessageConstants.NOT_READ);
        // 普通状态
        message.setmStatus(Status.NORMAL.name());
        // 将该信息设置到被回复的用户上
        // 新回复的id
        message.setReply(newReply);
        message.setPublisherUserId(newReply.getUserId());
        Episode episodeByEpId = episodeService.getEpisodeByEpId(newReply.getEpId());
        Bangumi bangumi = bangumiService.getBangumiById(episodeByEpId.getBangumiId());
        message.setTitle(bangumi.getBangumiName() + " " + episodeByEpId.getEpIndex());
        message.setContent(DmjiUtils.formatReplyContent(newReply.getContent()));
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        message.setCreateTime(ts);
        message.setModifyTime(ts);
        return message;
    }

    @Async
    public void sendDeleteReplyMessage(Reply reply){
        Message message = new Message();
        message.setmStatus(Status.NORMAL.name());
        message.setIsRead(MessageConstants.NOT_READ);
        message.setType(MessageType.SYSTEM.name());
        message.setReply(null);
        message.setTitle("评论删除通知");
        message.setUserId(reply.getUserId());
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        message.setCreateTime(ts);
        message.setModifyTime(ts);
        Episode episode = episodeService.getEpisodeByEpId(reply.getEpId());
        Bangumi bangumi = bangumiService.getBangumiById(episode.getBangumiId());

        String replyContent = "";
        if (reply.getContent().length()>30){
             replyContent = reply.getContent().substring(0,30)+"...";
        } else {
            replyContent = reply.getContent();
        }
        message.setContent("您在【"+bangumi.getBangumiName()+" "+episode.getEpIndex()+"】下的评论【"+replyContent+"】因举报已被删除，请遵守相关法律法规。");
        messageService.insertMessage(message);
    }

}
