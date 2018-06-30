package cc.dmji.api.service;

import cc.dmji.api.entity.Message;
import cc.dmji.api.enums.MessageStatus;
import cc.dmji.api.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Created by echisan on 2018/5/14
 */
public interface MessageService {

    Byte UN_READ = (byte)0;
    Byte READ = (byte)1;

    Message insertMessage(Message message);

    void deleteMessageById(String id);

    Message updateMessage(Message message);

    Message getMessageById(String id);

    Page<Message> listMessageByUserIdAndType(String userId, MessageType messageType, Integer pn, Integer ps);

    Page<Message> listMessageByUserId(String userId, Integer page,
                                      Integer size, @Nullable Sort sort);

    Long countByUserIdAndTypeAndIsRead(String userId, MessageType messageType, boolean isRead);

    List<Message> updateMessages(List<Message> messageList);

    List<Message> listUserUnReadMessages(String userId,MessageType messageType);

    List<Message> listMessages();

    List<Map<String,Long>> countUnReadMessageById(String userId);

    void deleteMessageByIds(List<String> ids);

    void deleteMessage(List<Message> messageList);

    void insertMessageList(List<Message> messageList);

}
