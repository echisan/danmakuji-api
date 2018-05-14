package cc.dmji.api.service;

import cc.dmji.api.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

/**
 * Created by echisan on 2018/5/14
 */
public interface MessageService {

    Message insertMessage(Message message);

    void deleteMessageById(String id);

    Message updateMessage(Message message);

    Message getMessageById(String id);

    Page<Message> listMessageByUserId(String userId, Integer page,
                                      Integer size, @Nullable Sort sort);

}
