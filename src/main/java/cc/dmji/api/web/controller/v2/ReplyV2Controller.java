package cc.dmji.api.web.controller.v2;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.LikeRecord;
import cc.dmji.api.entity.v2.ReplyV2;
import cc.dmji.api.enums.Direction;
import cc.dmji.api.enums.v2.ReplyOrderBy;
import cc.dmji.api.enums.v2.ReplyType;
import cc.dmji.api.service.LikeRecordService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.service.v2.ReplyV2Service;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.controller.BaseController;
import cc.dmji.api.web.listener.AtMessageEvent;
import cc.dmji.api.web.listener.LikeMessageEvent;
import cc.dmji.api.web.listener.ReplyMessageEvent;
import cc.dmji.api.web.model.v2.reply.ReplyDTO;
import cc.dmji.api.web.model.v2.reply.ReplyDetail;
import cc.dmji.api.web.model.v2.reply.ReplyResponse;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by echisan on 2018/7/26
 */
@RestController
@RequestMapping("/v2/replies")
public class ReplyV2Controller extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ReplyV2Controller.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ReplyV2Service replyV2Service;

    @Autowired
    private LikeRecordService likeRecordService;

    @Autowired
    private UserService userService;

    @PostMapping
    @UserLog("发送评论")
    public ResponseEntity<Result> postReply(HttpServletRequest request,
                                            @RequestBody Map<String, Object> requestMap) {
        // 用户id
        Long userId = getUidFromRequest(request);

        // requestMap 有参数: oid,type,content,|root
        Long objectId = null;
        Integer type = null;
        String content;
        Long root = null;
        ReplyType replyType = null;
        try {
            Object oid = requestMap.get("oid");
            if (oid instanceof Integer) {
                objectId = ((Integer) oid).longValue();
            } else if (oid instanceof Long) {
                objectId = (Long) oid;
            } else if (oid instanceof String) {
                objectId = Long.valueOf((String) oid);
            } else {
                return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "oid参数不正确");
            }

            type = (Integer) requestMap.get("type");
            content = (String) requestMap.get("content");
            if (requestMap.get("root") != null) {
                Object rootObject = requestMap.get("root");
                if (rootObject instanceof Integer) {
                    root = ((Integer) rootObject).longValue();
                } else if (rootObject instanceof Long) {
                    root = (Long) rootObject;
                } else if (rootObject instanceof String) {
                    root = Long.valueOf((String) rootObject);
                } else {
                    return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "root参数不正确");
                }
            }
            replyType = ReplyType.byCode(type);
            if (replyType == null) {
                return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "type参数不正确");
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            // 只要有一个参数转换不成功就返回错误
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "参数不正确");
        }

        Long floor;
        ReplyV2 rootReply = null;
        // 如果root为空，则这是一条父级评论
        if (root == null) {
            floor = replyV2Service.countNowFloorByObjectAndType(objectId, replyType);
            root = 0L;
        } else {
            // 存在root则是子评论
            // 判断该root是否存在
            ReplyV2 replyV2 = replyV2Service.getById(root);
            if (replyV2 == null) {
                return getResponseEntity(HttpStatus.NOT_FOUND, getErrorResult(ResultCode.RESULT_DATA_NOT_FOUND, "该评论不存在"));
            }
            rootReply = replyV2;
            floor = replyV2Service.countNowFloorByRootReplyId(root, replyType);
        }
        ReplyDTO replyDTO = new ReplyDTO(userId, replyType, objectId, content, root, floor + 1);
        ReplyV2 insertReplyV2 = replyV2Service.insert(replyDTO);
        logger.debug("insert reply, id:{}, userId:{}, content:{} ", insertReplyV2.getId(), insertReplyV2.getUserId(), insertReplyV2.getContent());
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("rpid", insertReplyV2.getId());
        resultMap.put("rpid_str", String.valueOf(insertReplyV2.getId()));


        /* ---------- 发送通知 ---------- */

        // 先判断有没有艾特的用户
        List<String> nickList = DmjiUtils.findAtUsername(insertReplyV2.getContent());
        // 最多支持at5个用户
        if (nickList != null && nickList.size() != 0) {
            nickList.remove(getNickFormRequest(request));
            AtMessageEvent event = new AtMessageEvent(this, null,userId,insertReplyV2,nickList);
            // 通知被艾特的用户
            applicationContext.publishEvent(event);
        }
        // 如果不是父级评论
        if (root != 0L) {
            ReplyMessageEvent event =
                    new ReplyMessageEvent(this, rootReply.getUserId(),userId,insertReplyV2);
            applicationContext.publishEvent(event);
        }

        return getSuccessResponseEntity(getSuccessResult(resultMap));
    }

    @GetMapping
    @UserLog("获取评论")
    public ResponseEntity<Result> listReplies(@RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                              @RequestParam(value = "type") Integer type,
                                              @RequestParam(value = "oid") Long oid,
                                              @RequestParam(value = "sort", required = false, defaultValue = "1") Integer sort,
                                              @RequestParam(value = "root", required = false, defaultValue = "0") Long root,
                                              @RequestParam(value = "rpid", required = false) Long rpid,
                                              HttpServletRequest request) {

        if (type == null || oid == null) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID);
        }

        ReplyType replyType = ReplyType.byCode(type);
        if (replyType == null) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "type不合法");
        }

        ReplyOrderBy replyOrderBy = ReplyOrderBy.byCode(sort);
        if (replyOrderBy == null) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "sort不合法");
        }

        if (pn <= 0) {
            return getErrorResponseEntity(HttpStatus.BAD_REQUEST, ResultCode.PARAM_IS_INVALID, "pn不合法");
        }

        Long uid = getUidFromRequest(request);
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setTop(replyV2Service.getTopReply(oid, replyType, uid));
        List<ReplyDetail> replies;
        // 如果没有replyId, 则是普通的查询
        if (rpid == null) {
            Page<ReplyDetail> replyDetailPage = PageHelper.startPage(pn, 20, true).doSelectPage(() -> {
                replyV2Service.listByObjectIdAndType(oid, replyType, uid, replyOrderBy, Direction.DESC);
            });
            replies = replyDetailPage.getResult();
            setSubReplies(replies, uid, oid, replyType);

            // 如果评论条数总数大于20条 且 root==0 时才显示热评
            if (replyDetailPage.getTotal() > 20 && root.equals(0L)) {
                Page<ReplyDetail> hotReplyPage = PageHelper.startPage(pn, 3, true).doSelectPage(() -> {
                    replyV2Service.listByObjectIdAndType(oid, replyType, root, uid, ReplyOrderBy.like_count, Direction.DESC);
                });
                List<ReplyDetail> collect = hotReplyPage.getResult()
                        .stream()
                        .filter(replyDetail -> !replyDetail.getLike().equals(0L))
                        .collect(Collectors.toList());
                if (collect.size() != 0) {
                    collect.forEach(rootReply -> {
                        Page<ReplyDetail> subReplies = PageHelper.startPage(1, 3, true).doSelectPage(() -> {
                            replyV2Service.listByObjectIdAndType(oid, replyType, rootReply.getId(), uid, ReplyOrderBy.floor, Direction.ASC);
                        });
                        rootReply.setReplies(subReplies);
                        rootReply.setReplyCount(subReplies.getTotal());
                    });
                    replyResponse.setHot(hotReplyPage.getResult());
                }
            }
            replyResponse.setPage(new PageInfo(pn, 20, replyDetailPage.getTotal()));
        } else {
            // 如果存在rpid的话，则是需要定位该评论的位置
            ReplyV2 replyV2 = replyV2Service.getById(rpid);
            if (replyV2 == null) {
                return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND);
            }
            // 判断该评论是根评论还是子评论

            // 如果是根评论
            if (replyV2.getRoot().equals(0L)) {
                Long replyCount = replyV2Service.countByObjectIdAndFloorBetween(oid, replyType, 1L, replyV2.getFloor());

                int rpn = Math.toIntExact((replyCount / 20) + 1);
                Page<ReplyDetail> replyDetailPage = PageHelper.startPage(rpn, 20, true).doSelectPage(() -> {
                    replyV2Service.listByObjectIdAndType(oid, replyType, uid, replyOrderBy, Direction.DESC);
                });
                replies = replyDetailPage.getResult();
                setSubReplies(replies, uid, oid, replyType);
                replyResponse.setPage(new PageInfo(pn, 20, replyDetailPage.getTotal()));
            } else {
                // 如果是子评论
                Long replyCount = replyV2Service.countByObjectIdAndFloorBetween(replyV2.getRoot(), replyType, 1L, replyV2.getFloor());
                int rpn = Math.toIntExact((replyCount / 20) + 1);
                Page<ReplyDetail> replyDetailPage = PageHelper.startPage(rpn, 20, true).doSelectPage(() -> {
                    replyV2Service.listByObjectIdAndType(oid, replyType, uid, replyOrderBy, Direction.DESC);
                });
                replies = replyDetailPage.getResult();
                setSubReplies(replies, uid, oid, replyType);

                replyResponse.setPage(new PageInfo(pn, 20, replyDetailPage.getTotal()));
                if (replyV2.getFloor() > 3 && replyV2.getFloor() < 10) {
                    replaceSubReplies(replies, oid, replyType, uid, root, 1);

                } else if (replyV2.getFloor() > 10) {
                    Long floorBetween = replyV2Service.countByRootAndFloorBetween(replyV2.getRoot(), replyType, 1L, replyV2.getFloor());
                    if (floorBetween <= 10) {
                        replaceSubReplies(replies, oid, replyType, uid, root, 1);
                    } else {
                        int page = Math.toIntExact((floorBetween / 10) + 1);
                        replaceSubReplies(replies, oid, replyType, uid, root, page);
                    }
                }
            }
        }
        replyResponse.setReplies(replies);
        return getSuccessResponseEntity(getSuccessResult(replyResponse));
    }

    @PostMapping("/{rpid}/like")
    @UserLog("点赞")
    public ResponseEntity<Result> likeReply(@PathVariable("rpid") Long rpid, HttpServletRequest request) {
        Long uid = getUidFromRequest(request);
        ReplyV2 replyV2 = replyV2Service.getById(rpid);
        if (replyV2 == null) {
            return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND);
        }
        LikeRecord likeRecord = likeRecordService.getByReplyIdAndUserId(replyV2.getId(), uid);
        // 如果点赞记录不为空，则查看是否已经点了赞了
        // 如果已点赞就不管了，未点赞的话就+1
        if (likeRecord != null) {
            if (!likeRecord.isLike()) {
                replyV2.setLikeCount(replyV2.getLikeCount() + 1);
            }
        } else {
            LikeRecord record = new LikeRecord();
            record.setLike(true);
            record.setReplyId(rpid);
            record.setUserId(uid);
            LikeRecord insertLikeRecord = likeRecordService.insertLikeRecord(record);
            replyV2.setLikeCount(replyV2.getLikeCount() + 1);
        }
        ReplyV2 update = replyV2Service.update(replyV2);
        logger.debug("like reply,id:{},like:{}", update.getId(), update.getLikeCount());

        // 通知用户被点赞了
        LikeMessageEvent event = new LikeMessageEvent(this,replyV2.getUserId(),uid,replyV2);
        applicationContext.publishEvent(event);

        return getSuccessResponseEntity(getSuccessResult());
    }

    @DeleteMapping("/{rpid}/like")
    @UserLog("取消点赞")
    public ResponseEntity<Result> unLikeReply(@PathVariable("rpid") Long rpid, HttpServletRequest request) {
        Long uid = getUidFromRequest(request);
        ReplyV2 replyV2 = replyV2Service.getById(rpid);
        if (replyV2 == null) {
            return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND);
        }
        LikeRecord likeRecord = likeRecordService.getByReplyIdAndUserId(replyV2.getId(), uid);

        if (likeRecord != null) {
            if (likeRecord.isLike()) {
                replyV2.setLikeCount(replyV2.getLikeCount() - 1);
                likeRecord.setLike(false);
                replyV2Service.update(replyV2);
                likeRecordService.updateLikeRecord(likeRecord);
                return getSuccessResponseEntity(getSuccessResult());
            }
        }
        return getSuccessResponseEntity(getSuccessResult("取消点赞失败，因为根本就没点过赞"));
    }

    /**
     * 分配子评论到父级评论
     *
     * @param replies root replies list
     * @param uid     user id
     * @param oid     object id
     * @param rt      reply type
     */
    private void setSubReplies(List<ReplyDetail> replies,
                               Long uid, Long oid, ReplyType rt) {
        replies.forEach(rootReply -> {
            // 子评论只查3条
            Page<ReplyDetail> subReplies = PageHelper.startPage(1, 3, true).doSelectPage(() -> {
                replyV2Service.listByObjectIdAndType(oid, rt, rootReply.getId(), uid, ReplyOrderBy.floor, Direction.ASC);
            });
            rootReply.setReplies(subReplies);
            rootReply.setReplyCount(subReplies.getTotal());
        });
    }

    /**
     * 当根据评论id定位评论所在的位置时
     * 假如该评论是子评论时，需要判断子评论的页码
     * 这里就需要调用本方法，将目标评论的整页子评论查出来
     * 并替换掉原来的子评论列表
     *
     * @param replies    root replies list
     * @param oid        object id
     * @param replyType  reply type
     * @param uid        uid
     * @param root       父级评论的id
     * @param pageNumber 子评论所在的页码
     */
    private void replaceSubReplies(List<ReplyDetail> replies, Long oid, ReplyType replyType, Long uid, Long root, Integer pageNumber) {
        Page<ReplyDetail> subReplyPage = PageHelper.startPage(pageNumber, 10, true).doSelectPage(() -> {
            replyV2Service.listByObjectIdAndType(oid, replyType, root, uid, ReplyOrderBy.floor, Direction.ASC);
        });
        for (int i = 0; i < replies.size(); i++) {
            if (replies.get(i).getId().equals(root)) {
                ReplyDetail replyDetail = replies.get(i);
                replyDetail.setReplies(subReplyPage.getResult());
                replies.set(i, replyDetail);
                break;
            }
        }
    }
}
