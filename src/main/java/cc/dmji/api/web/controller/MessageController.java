package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cc.dmji.api.constants.MessageConstants;
import cc.dmji.api.entity.Message;
import cc.dmji.api.entity.User;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.service.MessageService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.utils.DmjiUtils;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.web.model.MessageInfo;
import cc.dmji.api.web.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by echisan on 2018/6/25
 */
@RestController
@RequestMapping("/messages")
public class MessageController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}/countInfo")
    public ResponseEntity<Result> getUserMessageInfo(@PathVariable("userId") String userId) {

        String currentUserId = getUidFromToken(request);
        if (!currentUserId.equals(userId)) {
            return getResponseEntity(HttpStatus.FORBIDDEN, getErrorResult(ResultCode.PERMISSION_DENY));
        }

        // 未读回复通知
        Long unReadReplyMessageCount = messageService.countByUserIdAndTypeAndIsRead(userId, MessageType.REPLY, false);
        // 未读系统通知
        Long unReadSystemMessageCount = messageService.countByUserIdAndTypeAndIsRead(userId, MessageType.SYSTEM, false);
        Long unReadLikeMessageCount = messageService.countByUserIdAndTypeAndIsRead(userId, MessageType.LIKE, false);
        Long unReadAtMessageCount = messageService.countByUserIdAndTypeAndIsRead(userId, MessageType.AT, false);
        Long totalUnread = unReadReplyMessageCount + unReadSystemMessageCount + unReadLikeMessageCount + unReadAtMessageCount;

        Map<String, Long> data = new HashMap<>();
        data.put("reply", unReadReplyMessageCount);
        data.put("system", unReadSystemMessageCount);
        data.put("at", unReadAtMessageCount);
        data.put("like", unReadLikeMessageCount);
        data.put("total", totalUnread);

        return getResponseEntity(HttpStatus.OK, getSuccessResult(data));
    }

    @GetMapping("/{userId}/type/{type}")
    @UserLog("获取用户消息列表")
    public ResponseEntity<Result> listUserMessages(@PathVariable("userId") String userId,
                                                   @PathVariable("type") String type,
                                                   @RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
                                                   @RequestParam(value = "ps", required = false, defaultValue = "20") Integer ps) {

        MessageType messageType = null;
        // 如果是回复
        if (type.equalsIgnoreCase(MessageType.REPLY.name())) {
            messageType = MessageType.REPLY;
        } else if (type.equalsIgnoreCase(MessageType.SYSTEM.name())) {
            messageType = MessageType.SYSTEM;
        } else if (type.equalsIgnoreCase(MessageType.COMMENT.name())) {
            messageType = MessageType.COMMENT;
        } else if (type.equalsIgnoreCase(MessageType.LIKE.name())) {
            messageType = MessageType.LIKE;
        } else if (type.equalsIgnoreCase(MessageType.AT.name())) {
            messageType = MessageType.AT;
        }

        if (messageType == null) {
            return getResponseEntity(HttpStatus.BAD_REQUEST, getErrorResult(ResultCode.PARAM_IS_INVALID, "不支持的type:" + type));
        }

        Integer page = pn == 1 ? 0 : pn - 1;
        Page<Message> messagePage = messageService.listMessageByUserIdAndType(userId, messageType, page, ps);

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNumber(pn);
        pageInfo.setPageSize(ps);
        pageInfo.setTotalSize(messagePage.getTotalElements());
        List<Message> replyMessages = messagePage.getContent();
        // 获取用户id列表
        List<String> userIds = new ArrayList<>();
        replyMessages.forEach(message -> userIds.add(message.getPublisherUserId()));
        List<User> users = userService.listUserByIdsIn(userIds);
        Map<String, User> userMap = new HashMap<>();
        users.forEach(user -> userMap.put(user.getUserId(), user));

        List<MessageInfo> messageInfos = new ArrayList<>();
        replyMessages.forEach(message -> {
            MessageInfo messageInfo = new MessageInfo(message);
            if (message.getReply() != null){
                messageInfo.setReplyId(message.getReply().getReplyId());
                messageInfo.setEpId(message.getReply().getEpId());
            }
            messageInfo.setContent(message.getContent());
            messageInfo.setTitle(message.getTitle());
            if (!StringUtils.isEmpty(message.getPublisherUserId())){
                User user = userMap.get(message.getPublisherUserId());
                if (user != null) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setNick(user.getNick());
                    userInfo.setUid(user.getUserId());
                    userInfo.setFace(user.getFace());
                    userInfo.setSex(user.getSex());
                    messageInfo.setUserInfo(userInfo);
                } else {
                    messageInfo.setUserInfo(new UserInfo());
                }
            }
            messageInfos.add(messageInfo);
        });

        Map<String, Object> data = new HashMap<>();
        data.put("pageInfo", pageInfo);
        data.put("messages", messageInfos);

        // 将该用户的某个type的消息都设置成已读
        cleanUnReadMessage(userId, messageType);
        return getResponseEntity(HttpStatus.OK, getSuccessResult(data));
    }

    @Async
    public void cleanUnReadMessage(String userId, MessageType type) {
        List<Message> messages = messageService.listUserUnReadMessages(userId, type);
        if (messages.size() != 0) {
            messages.forEach(message -> message.setIsRead(MessageConstants.IS_READ));
            List<Message> messages1 = messageService.updateMessages(messages);
            logger.debug("已将id为{}的用户的未读信息设置为已读,共{}条", userId, messages1.size());
        }
    }



}
