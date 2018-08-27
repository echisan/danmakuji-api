package cc.dmji.api.service.impl.v2;

import cc.dmji.api.entity.v2.MessageV2;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.enums.Status;
import cc.dmji.api.mapper.v2.MessageMapper;
import cc.dmji.api.repository.v2.MessageV2Repository;
import cc.dmji.api.service.v2.MessageV2Service;
import cc.dmji.api.web.model.v2.message.MessageDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MessageV2ServiceImpl implements MessageV2Service {

    @Autowired
    private MessageV2Repository messageV2Repository;
    @Resource
    private MessageMapper messageMapper;

    @Override
    public MessageV2 getById(Long messageId) {
        return messageV2Repository.findById(messageId).orElse(null);
    }

    @Override
    @Transactional
    public MessageV2 update(MessageV2 messageV2) {
        return messageV2Repository.save(messageV2);
    }

    @Override
    @Transactional
    public List<MessageV2> insertAll(List<MessageV2> messageV2List) {
        return messageV2Repository.saveAll(messageV2List);
    }

    @Override
    @Transactional
    public MessageV2 insert(MessageV2 messageV2) {
        return messageV2Repository.save(messageV2);
    }

    @Override
    public List<MessageDetail> listMessages(Long uid, MessageType messageType) {
        return messageMapper.listMessages(uid, Status.NORMAL.ordinal(),messageType.getCode());
    }

    @Override
    public List<MessageV2> listUserUnReadMessage(Long uid, MessageType messageType) {
        return messageV2Repository.findByUidEqualsAndTypeEqualsAndReadEquals(uid,messageType.getCode(),false);
    }

    @Override
    public List<MessageV2> listUserUnReadMessage(Long uid) {
        return messageV2Repository.findByUidEqualsAndReadEquals(uid,false);
    }

    @Override
    public Long countUnreadMessage(Long uid, MessageType messageType) {
        return messageV2Repository.countByUidEqualsAndTypeEqualsAndReadEquals(uid,messageType.getCode(),false);
    }

    @Override
    public List<MessageV2> listIdIn(List<Long> mids) {
        return messageV2Repository.findByIdIn(mids);
    }
}
