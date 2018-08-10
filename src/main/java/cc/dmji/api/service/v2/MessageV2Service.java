package cc.dmji.api.service.v2;

import cc.dmji.api.entity.v2.MessageV2;

import java.util.List;

public interface MessageV2Service {

    List<MessageV2> insertAll(List<MessageV2> messageV2List);
    MessageV2 insert(MessageV2 messageV2);
}
