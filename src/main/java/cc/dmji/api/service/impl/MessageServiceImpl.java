package cc.dmji.api.service.impl;

import cc.dmji.api.entity.Message;
import cc.dmji.api.repository.MessageRepository;
import cc.dmji.api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * Created by echisan on 2018/5/14
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Message insertMessage(Message message) {
        Message m = new Message();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        m.setUserId(message.getUserId());
        m.setCreateTime(ts);
        m.setModifyTime(ts);
        m.setIsRead(message.getIsRead());
        m.setmStatus(message.getmStatus());
        m.setAtAnchor(message.getAtAnchor());
        m.setType(message.getType());
        m.setReplyId(message.getReplyId());
        m.setEpId(message.getEpId());
        return messageRepository.save(m);
    }

    @Override
    public void deleteMessageById(String id) {
        messageRepository.deleteById(id);
    }

    @Override
    public Message updateMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Message getMessageById(String id) {
        return messageRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Message> listMessageByUserId(String userId, Integer page, Integer size, Sort sort) {
        Sort mSort = null;
        if (sort == null) {
            mSort = new Sort(Sort.Direction.DESC, "createTime");
        } else {
            mSort = sort;
        }
        Pageable pageable = PageRequest.of(page, size, mSort);
        return messageRepository.findByUserIdEquals(userId, pageable);
    }
}
