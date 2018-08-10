package cc.dmji.api.service.impl.v2;

import cc.dmji.api.entity.v2.MessageV2;
import cc.dmji.api.repository.v2.MessageV2Repository;
import cc.dmji.api.service.v2.MessageV2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageV2ServiceImpl implements MessageV2Service {
    @Autowired
    private MessageV2Repository messageV2Repository;
    @Override
    public List<MessageV2> insertAll(List<MessageV2> messageV2List) {
        return messageV2Repository.saveAll(messageV2List);
    }

    @Override
    public MessageV2 insert(MessageV2 messageV2) {
        return messageV2Repository.save(messageV2);
    }
}
