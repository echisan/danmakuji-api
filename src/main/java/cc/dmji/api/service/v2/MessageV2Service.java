package cc.dmji.api.service.v2;

import cc.dmji.api.entity.v2.MessageV2;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.web.model.v2.message.MessageDetail;

import java.util.List;

public interface MessageV2Service {

    MessageV2 getById(Long messageId);

    List<MessageV2> insertAll(List<MessageV2> messageV2List);

    MessageV2 insert(MessageV2 messageV2);

    MessageV2 update(MessageV2 messageV2);

    List<MessageDetail> listMessages(Long uid, MessageType messageType);

    List<MessageV2> listUserUnReadMessage(Long uid, MessageType messageType);

    List<MessageV2> listUserUnReadMessage(Long uid);

    Long countUnreadMessage(Long uid, MessageType messageType);
}
