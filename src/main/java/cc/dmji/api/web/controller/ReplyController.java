package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.MessageConstants;
import cc.dmji.api.constants.ReplyConstants;
import cc.dmji.api.entity.Message;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.entity.Reply;
import cc.dmji.api.entity.Status;
import cc.dmji.api.service.MessageService;
import cc.dmji.api.service.ReplyService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.model.ReplyInfo;
import cc.dmji.api.web.model.ReplyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by echisan on 2018/5/25
 */
@RestController
@RequestMapping("/replies")
public class ReplyController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ReplyController.class);

    @Autowired
    private ReplyService replyService;

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<Result> listEpReplies(@RequestParam Integer epId,
                                                @RequestParam(value = "pn", defaultValue = "1", required = false) Integer pn) {
        if (epId == null) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_BLANK));
        }
        Map<String, Object> data = replyService.listEpisodeReplies(epId, pn);

        return getResponseEntity(HttpStatus.OK, getSuccessResult(data));
    }

    @PostMapping
    public ResponseEntity<Result> addReply(@RequestBody ReplyRequest replyRequest) {

        boolean isReplyParent = false;

        if (StringUtils.isEmpty(replyRequest.getContent())) {
            return getResponseEntity(
                    HttpStatus.BAD_REQUEST,
                    getErrorResult(ResultCode.PARAM_IS_INVALID, "评论/回复不能为空")
            );

        }

        Reply reply = new Reply();

        // 清除xss
        String content = DmjiUtils.htmlEncode(replyRequest.getContent());


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
            reply.setParentId("");
            isReplyParent = true;
        } else {
            // 将回复设置成不是父级评论
            reply.setIsParent(ReplyConstants.NOT_PARENT);
            // 设置该回复的父级评论id
            reply.setParentId(replyRequest.getP_rid());

        }
        // 初始化评论的赞踩
        reply.setrLike(0);
        reply.setrHate(0);

        Reply newReply = replyService.insertReply(reply);
        logger.info("new Reply:{}", newReply.toString());

        // 如果是父级评论则直接返回该评论信息
        if (isReplyParent) {
            return getResponseEntity(
                    HttpStatus.OK,
                    getSuccessResult(newReply)
            );
        }
        // 如果不是父级评论，则通知该父级评论用户

        // 消息通知
        Message message = new Message();
        // 如果不是父级评论
        message.setType(MessageType.REPLY.name());
        // 设置该评论所在的集数
        message.setEpId(reply.getEpId());
        // 没读
        message.setIsRead(MessageConstants.NOT_READ);
        // 普通状态
        message.setmStatus(Status.NORMAL.name());
        message.setAtAnchor("");
        // 将该信息设置到被回复的用户上
        message.setUserId(replyRequest.getP_uid());
        message.setReplyId(newReply.getReplyId());

        Message newMessage = messageService.insertMessage(message);

        logger.info("new Message:{}", newMessage.toString());

        return getResponseEntity(HttpStatus.OK, getSuccessResult(newReply));
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Result> deleteReplyById(@PathVariable String replyId){
        replyService.deleteReplyById(replyId);
        return getResponseEntity(HttpStatus.OK,getSuccessResult());
    }

    /**
     * 点赞功能
     * @return
     */
    @PostMapping("/action")
    public ResponseEntity<Result> doActionAtReply(){
        return getResponseEntity(HttpStatus.OK,getSuccessResult());
    }
}
