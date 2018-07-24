package cc.dmji.api.service.impl;

import cc.dmji.api.constants.MessageConstants;
import cc.dmji.api.entity.Message;
import cc.dmji.api.entity.User;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.repository.MessageRepository;
import cc.dmji.api.service.MessageService;
import cc.dmji.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by echisan on 2018/5/14
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Override
    public Message insertMessage(Message message) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        message.setCreateTime(ts);
        message.setModifyTime(ts);
        return messageRepository.save(message);
    }

    @Override
    public void deleteMessageById(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    public Message updateMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Message getMessageById(Long id) {
        return messageRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Message> listMessageByUserIdAndType(Long userId, MessageType messageType, Integer pn, Integer ps) {
        PageRequest pageRequest = PageRequest.of(pn, ps, new Sort(Sort.Direction.DESC, "createTime"));
        if (messageType.equals(MessageType.SYSTEM)){
            User user = userService.getUserById(userId);
            return messageRepository.findByUserIdEqualsAndTypeEqualsOrUserIdIsNullAndCreateTimeAfter(userId,messageType.name(),user.getCreateTime(),pageRequest);
        }
        return messageRepository.findByUserIdEqualsAndTypeEquals(userId, messageType.name(), pageRequest);
    }

    @Override
    public Long countByUserIdAndTypeAndIsRead(Long userId, MessageType messageType, boolean isRead) {
        Byte isReadByte = isRead ? READ : UN_READ;
        return messageRepository.countByUserIdEqualsAndTypeEqualsAndIsRead(userId, messageType.name(), isReadByte);
    }

    @Override
    public Page<Message> listMessageByUserId(Long userId, Integer page, Integer size, Sort sort) {
        Sort mSort = null;
        if (sort == null) {
            mSort = new Sort(Sort.Direction.DESC, "createTime");
        } else {
            mSort = sort;
        }
        Pageable pageable = PageRequest.of(page, size, mSort);
        return messageRepository.findByUserIdEquals(userId, pageable);
    }

    @Override
    public List<Message> listMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> updateMessages(List<Message> messageList) {
        return messageRepository.saveAll(messageList);
    }

    @Override
    public List<Message> listUserUnReadMessages(Long userId,MessageType messageType) {
        return messageRepository.findByUserIdEqualsAndIsReadEqualsAndTypeEquals(userId,MessageConstants.NOT_READ,messageType.name());
    }

    @Override
    public List<Map<String, Long>> countUnReadMessageById(Long userId) {
        return null;
    }

    @Override
    public void deleteMessageByIds(List<Long> ids) {
        messageRepository.deleteByIdIn(ids);
    }

    @Override
    public void deleteMessage(List<Message> messageList) {
        messageRepository.deleteInBatch(messageList);
    }

    @Override
    public void insertMessageList(List<Message> messageList) {
        messageRepository.saveAll(messageList);
    }
}
