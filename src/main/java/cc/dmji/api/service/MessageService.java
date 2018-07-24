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

    void deleteMessageById(Long id);

    Message updateMessage(Message message);

    Message getMessageById(Long id);

    Page<Message> listMessageByUserIdAndType(Long userId, MessageType messageType, Integer pn, Integer ps);

    Page<Message> listMessageByUserId(Long userId, Integer page,
                                      Integer size, @Nullable Sort sort);

    Long countByUserIdAndTypeAndIsRead(Long userId, MessageType messageType, boolean isRead);

    List<Message> updateMessages(List<Message> messageList);

    List<Message> listUserUnReadMessages(Long userId,MessageType messageType);

    List<Message> listMessages();

    List<Map<String,Long>> countUnReadMessageById(Long userId);

    void deleteMessageByIds(List<Long> ids);

    void deleteMessage(List<Message> messageList);

    void insertMessageList(List<Message> messageList);

}
