package cc.dmji.api.web.controller.v2;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.entity.LikeRecord;
import cc.dmji.api.entity.User;
import cc.dmji.api.entity.v2.ReplyV2;
import cc.dmji.api.enums.Direction;
import cc.dmji.api.enums.Role;
import cc.dmji.api.enums.Status;
import cc.dmji.api.enums.v2.ReplyOrderBy;
import cc.dmji.api.enums.v2.ReplyType;
import cc.dmji.api.service.LikeRecordService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.service.v2.ReplyV2Service;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.JwtUserInfo;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.controller.BaseController;
import cc.dmji.api.web.listener.AtMessageEvent;
import cc.dmji.api.web.listener.DeleteReplyMessageEvent;
import cc.dmji.api.web.listener.LikeMessageEvent;
import cc.dmji.api.web.listener.ReplyMessageEvent;
import cc.dmji.api.web.model.v2.reply.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

        Object tuidObject = requestMap.get("tuid");
        Long tuid;
        if (tuidObject instanceof Integer) {
            tuid = ((Integer) tuidObject).longValue();
        } else if (tuidObject instanceof Long) {
            tuid = (Long) tuidObject;
        } else {
            tuid = null;
        }
        // 先判断有没有艾特的用户
        List<String> nickList = DmjiUtils.findAtUsername(insertReplyV2.getContent());
        if (tuid != null && !tuid.equals(userId)) {
            User subReplyTargetUser = userService.getUserById(tuid);
            nickList.remove(subReplyTargetUser.getNick());
            applicationContext.publishEvent(new ReplyMessageEvent(this, tuid, userId, insertReplyV2));
        }
        // 最多支持at5个用户
        if (nickList != null && nickList.size() != 0) {
            nickList.remove(getNickFormRequest(request));
            AtMessageEvent event =
                    new AtMessageEvent(this, rootReply == null ? null : rootReply.getUserId(), userId, insertReplyV2, nickList);
            // 通知被艾特的用户
            applicationContext.publishEvent(event);
        }
        // 如果不是父级评论
        if (root != 0L && rootReply != null && !rootReply.getUserId().equals(userId)) {
            if (tuid == null) {
                ReplyMessageEvent event =
                        new ReplyMessageEvent(this, rootReply.getUserId(), userId, insertReplyV2);
                applicationContext.publishEvent(event);
            }
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
        ReplyDetail topReply = null;
        if (pn==1){
            replyResponse.setTop((topReply = replyV2Service.getTopReply(oid, replyType, uid)));
        }
        List<ReplyDetail> replies;
        JumpSubPageInfo subPageInfo = new JumpSubPageInfo(0, 0, 0L, 0L);

        //评论分页信息
        PageInfo pageInfo = new PageInfo(pn, 20, 0);
        // 如果没有replyId, 则是普通的查询
        if (rpid == null) {
            if (root > 0) {
                SubReplyResponse subReplyResponse = new SubReplyResponse();
                Page<ReplyDetail> subReplies = PageHelper.startPage(pn, 10, true).doSelectPage(() -> {
                    replyV2Service.listByObjectIdAndType(oid, replyType, root, uid, ReplyOrderBy.floor, Direction.ASC);
                });
                subReplyResponse.setReplies(subReplies.getResult());
                //子评论不需要设置allTotalSize
                subReplyResponse.setPage(new PageInfo(pn, 10, subReplies.getTotal()));
                subReplyResponse.setRoot(replyV2Service.getTopReply(root, replyType, uid));
                return getSuccessResponseEntity(getSuccessResult(subReplyResponse));
            }

            Page<ReplyDetail> replyDetailPage = PageHelper.startPage(pn, 20, true).doSelectPage(() -> {
                replyV2Service.listByObjectIdAndType(oid, replyType, uid, replyOrderBy, Direction.DESC);
            });
            replies = replyDetailPage.getResult();
            setSubReplies(replies, uid, oid, replyType);
            // 去重，在评论列表里去掉置顶的，以及为置顶评论设置自评论
            if (topReply != null) {
                int i = -1;
                for (int j = 0; j < replies.size(); j++) {
                    if (replies.get(j).getId().equals(topReply.getId())) {
                        i = j;
                        break;
                    }
                }
                if (i != -1) {
                    replyResponse.setTop(replies.get(i));
                    replies.remove(i);
                } else {
                    // 如果该置顶评论不在第一页的话就要把子评论查询出来
                    // 再设置到置顶评论当中
                    ReplyDetail finalTopReply = topReply;
                    Page<ReplyDetail> topSubReplies = PageHelper.startPage(1, 3, true).doSelectPage(() -> {
                        replyV2Service.listByObjectIdAndType(oid, replyType, finalTopReply.getId(), uid, ReplyOrderBy.floor, Direction.ASC);
                    });
                    topReply.setReplies(topSubReplies.getResult());
                    topReply.setReplyCount(topSubReplies.getTotal());
                    replyResponse.setTop(topReply);
                }
            }
            // 如果评论条数总数大于20条 且 第一页 且 root==0 时才显示热评 ,如果是按热度排行的话 就不加载热评了
            if (replyDetailPage.getTotal() > 20 && root.equals(0L) && pn == 1 && replyOrderBy.equals(ReplyOrderBy.create_time)) {
                Page<ReplyDetail> hotReplyPage = PageHelper.startPage(pn, 3, true).doSelectPage(() -> {
                    replyV2Service.listByObjectIdAndType(oid, replyType, root, uid, ReplyOrderBy.like_count, Direction.DESC);
                });

                // 去重
                List<ReplyDetail> collect = hotReplyPage.getResult()
                        .stream()
                        // 评论点赞要大于5个点赞才算
                        .filter(replyDetail -> replyDetail.getLike() > 5)
                        .collect(Collectors.toList());
                if (collect.size() != 0) {
                    collect.forEach(rootReply -> {
                        Page<ReplyDetail> subReplies = PageHelper.startPage(1, 3, true).doSelectPage(() -> {
                            replyV2Service.listByObjectIdAndType(oid, replyType, rootReply.getId(), uid, ReplyOrderBy.floor, Direction.ASC);
                        });
                        rootReply.setReplies(subReplies);
                        rootReply.setReplyCount(subReplies.getTotal());
                    });
                    // 去重
                    List<ReplyDetail> requireRemove = new ArrayList<>();
                    hotReplyPage.getResult().forEach(hrd -> {
                        replies.forEach(rrd -> {
                            if (rrd.getId().equals(hrd.getId())) {
                                requireRemove.add(rrd);
                            }
                        });
                    });
                    List<ReplyDetail> hotResult = hotReplyPage.getResult();
                    hotResult.removeAll(requireRemove);
                    replyResponse.setHot(hotResult);
                }
            }

            pageInfo.setTotalSize(replyDetailPage.getTotal());
            pageInfo.setAllTotalSize(replyV2Service.countAllRepliesByObjectIdAndReplyType(oid, replyType, Status.NORMAL));
            replyResponse.setPage(pageInfo);
        } else {
            // 如果存在rpid的话，则是需要定位该评论的位置
            ReplyV2 replyV2 = replyV2Service.getById(rpid);
            if (replyV2 == null) {
                return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND);
            }
            subPageInfo.setRootId(replyV2.getRoot());
            // 判断该评论是根评论还是子评论

            // 如果是根评论
            if (replyV2.getRoot().equals(0L)) {
                // 获取该oid，type下的最新的一条评论
                Page<ReplyDetail> newestReplyPage = PageHelper.startPage(1, 1, false).doSelectPage(() ->
                        replyV2Service.listByObjectIdAndType(oid, replyType, 0L, uid, ReplyOrderBy.create_time, Direction.DESC)
                );
                ReplyDetail newestReply = newestReplyPage.getResult().size() != 0 ? newestReplyPage.get(0) : null;

                // 计算最新一条评论与目标评论楼层数的之间的差
                Long replyCount = replyV2Service.countByObjectIdAndFloorBetween(oid, replyType, replyV2.getFloor(), newestReply == null ? 0L : newestReply.getFloor());

                int rpn = replyCount <= 20 ? 1 : Math.toIntExact((replyCount / 20) + 1);
                Page<ReplyDetail> replyDetailPage = PageHelper.startPage(rpn, 20, true).doSelectPage(() -> {
                    replyV2Service.listByObjectIdAndType(oid, replyType, uid, replyOrderBy, Direction.DESC);
                });
                replies = replyDetailPage.getResult();
                setSubReplies(replies, uid, oid, replyType);
                //将所有子评论数与根评论数相加
                pageInfo.setTotalSize(replyDetailPage.getTotal());
                pageInfo.setAllTotalSize(replyV2Service.countAllRepliesByObjectIdAndReplyType(oid, replyType, Status.NORMAL));
                replyResponse.setPage(pageInfo);
            } else {
                // 如果是子评论，查出该父级评论所在的页码
                ReplyV2 rootReply = replyV2Service.getById(replyV2.getRoot());
                Page<ReplyDetail> newestReplyPage = PageHelper.startPage(1, 1, false).doSelectPage(() ->
                        replyV2Service.listByObjectIdAndType(oid, replyType, 0L, uid, ReplyOrderBy.create_time, Direction.DESC)
                );
                ReplyDetail newestReply = newestReplyPage.getResult().size() != 0 ? newestReplyPage.get(0) : null;
                // 计算最新一条评论与父级评论楼层数的之间的差
                Long replyCount = replyV2Service.countByObjectIdAndFloorBetween(oid, replyType, rootReply.getFloor(), newestReply == null ? 0L : newestReply.getFloor());
                int rpn = replyCount <= 20 ? 1 : Math.toIntExact((replyCount / 20) + 1);
                pageInfo.setPageNumber(rpn);
                Page<ReplyDetail> replyDetailPage = PageHelper.startPage(rpn, 20, true).doSelectPage(() -> {
                    replyV2Service.listByObjectIdAndType(oid, replyType, uid, replyOrderBy, Direction.DESC);
                });
                replies = replyDetailPage.getResult();
                setSubReplies(replies, uid, oid, replyType);
                //将所有子评论数与根评论数相加
                pageInfo.setTotalSize(replyDetailPage.getTotal());
                pageInfo.setAllTotalSize(replyV2Service.countAllRepliesByObjectIdAndReplyType(oid, replyType, Status.NORMAL));
                replyResponse.setPage(pageInfo);
                if (replyV2.getFloor() > 3 && replyV2.getFloor() < 10) {
                    replaceSubReplies(replies, oid, replyType, uid, replyV2.getRoot(), 1, subPageInfo);

                } else if (replyV2.getFloor() >= 10) {
                    Long floorBetween = replyV2Service.countByRootAndFloorBetween(replyV2.getRoot(), replyType, 1L, replyV2.getFloor());
                    if (floorBetween <= 10) {
                        replaceSubReplies(replies, oid, replyType, uid, replyV2.getRoot(), 1, subPageInfo);
                    } else {
                        int page = Math.toIntExact((floorBetween / 10) + 1);
                        replaceSubReplies(replies, oid, replyType, uid, replyV2.getRoot(), page, subPageInfo);
                    }
                }

            }
        }
        replyResponse.setReplies(replies);
        if (rpid == null) {
            return getSuccessResponseEntity(getSuccessResult(replyResponse));
        } else {
            Map<String, Object> replyResponseMap = new HashMap<>();
            replyResponseMap.put("replies", replyResponse.getReplies());
            replyResponseMap.put("hot", replyResponse.getHot());
            replyResponseMap.put("top", replyResponse.getHot());
            replyResponseMap.put("page", replyResponse.getPage());
            replyResponseMap.put("subpage", subPageInfo);
            return getSuccessResponseEntity(getSuccessResult(replyResponseMap));
        }

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

        if (likeRecord == null) {
            // 通知用户被点赞了
            LikeMessageEvent event = new LikeMessageEvent(this, replyV2.getUserId(), uid, replyV2);
            applicationContext.publishEvent(event);
        }

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

    @DeleteMapping("/{rpid}")
    public ResponseEntity<Result> deleteReply(@PathVariable("rpid") Long rpid,
                                              HttpServletRequest request) {
        ReplyV2 replyV2 = replyV2Service.getById(rpid);
        if (replyV2 == null) {
            return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND, "需要操作的对象不存在");
        }

        JwtUserInfo jwtUserInfo = getJwtUserInfo(request);
        // 如果是管理员的话
        if (!jwtUserInfo.getRole().equals(Role.USER)) {
            replyV2.setStatus(Status.DELETE);
            ReplyV2 deleteReply = replyV2Service.update(replyV2);
            applicationContext.publishEvent(
                    new DeleteReplyMessageEvent(this, deleteReply.getUserId(), jwtUserInfo.getUid(), deleteReply)
            );
            return getSuccessResponseEntity(getSuccessResult());
        }

        if (!replyV2.getUserId().equals(jwtUserInfo.getUid())) {
            return getErrorResponseEntity(HttpStatus.FORBIDDEN, ResultCode.PERMISSION_DENY);
        }

        replyV2.setStatus(Status.DELETE);
        ReplyV2 deleteReply = replyV2Service.update(replyV2);
        logger.debug("删除的评论:", deleteReply);
        return getSuccessResponseEntity(getSuccessResult());
    }

    @PostMapping("/{rpid}/top")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Result> setReplyTop(@PathVariable("rpid") Long rpid, HttpServletRequest request) {
        JwtUserInfo jwtUserInfo = getJwtUserInfo(request);

        ReplyV2 replyV2 = replyV2Service.getById(rpid);
        if (replyV2 == null) {
            return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND, "找不到需要被置顶的评论");
        }

        ReplyV2 topReply = replyV2Service.getTopReply(replyV2.getObjectId(), replyV2.getReplyType());
        if (topReply != null) {
            topReply.setTop(false);
            replyV2Service.update(topReply);
        }

        replyV2.setTop(true);
        ReplyV2 update = replyV2Service.update(replyV2);
        logger.debug("将id为:{}的评论设置成置顶，置顶状态：", update.getId(), update.isTop());

        //applicationContext.publishEvent(new TopReplyMessageEvent(this,replyV2.getUserId(),jwtUserInfo.getUid(),update));
        return getSuccessResponseEntity(getSuccessResult());
    }

    @DeleteMapping("/{rpid}/top")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Result> cancelTopReply(@PathVariable("rpid") Long rpid,
                                                 HttpServletRequest request) {
        ReplyV2 replyV2 = replyV2Service.getById(rpid);
        if (replyV2 == null) {
            return getErrorResponseEntity(HttpStatus.NOT_FOUND, ResultCode.RESULT_DATA_NOT_FOUND, "找不到被取消置顶的评论");
        }
        replyV2.setTop(false);
        ReplyV2 update = replyV2Service.update(replyV2);
        logger.debug("将id为:{}的评论取消置顶，置顶状态：", update.getId(), update.isTop());
        return getSuccessResponseEntity(getSuccessResult());
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
            //将所有根评论的子评论累加，存储到pageInfo的allTotalSize变量上
//            pageInfo.setAllTotalSize(pageInfo.getAllTotalSize()+subReplies.getTotal());
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
     * @param pageInfo   该子评论的分页信息
     */
    private void replaceSubReplies(List<ReplyDetail> replies, Long oid, ReplyType replyType,
                                   Long uid, Long root, Integer pageNumber, JumpSubPageInfo pageInfo) {
        Page<ReplyDetail> subReplyPage = PageHelper.startPage(pageNumber, 10, true).doSelectPage(() -> {
            replyV2Service.listByObjectIdAndType(oid, replyType, root, uid, ReplyOrderBy.floor, Direction.ASC);
        });
        for (int i = 0; i < replies.size(); i++) {
            if (replies.get(i).getId().equals(root)) {
                ReplyDetail replyDetail = replies.get(i);
                replyDetail.setReplies(subReplyPage.getResult());
                replies.set(i, replyDetail);
                pageInfo.setRootId(replies.get(i).getId());
                break;
            }
        }
        pageInfo.setPageNumber(pageNumber);
        pageInfo.setPageSize(10);
        pageInfo.setTotalSize(subReplyPage.getTotal());

    }
}
