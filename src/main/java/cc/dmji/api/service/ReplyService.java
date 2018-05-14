package cc.dmji.api.service;

import cc.dmji.api.entity.Reply;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReplyService  {

    Reply insertReply(Reply reply);

    void deleteReplyById(String id);

    List<Reply> listReplyByEpId(Integer epId);

    Reply getReplyById(String id);

    Page<Reply> listReplyByEpId(Integer epId, Integer page, Integer size);

}
