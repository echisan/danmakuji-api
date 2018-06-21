package cc.dmji.api.service;

import cc.dmji.api.entity.Reply;
import cc.dmji.api.web.model.Replies;
import cc.dmji.api.web.model.ReplyInfo;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReplyService  {

    Reply insertReply(Reply reply);

    void deleteReplyById(String id);

    List<Reply> listReplyByEpId(Integer epId);

    Reply getReplyById(String id);

    Long countReplyByEpId(Integer epId);

    Page<Reply> listReplyByEpId(Integer epId, Integer page, Integer size);


    /**
     * 获取指定集数下的评论
     * @param epId 集数id
     * @param pn 页数
     * @param ps 每页获取的大小
     * @return
     */
    List<Replies> listEpisodeReplies(Integer epId, Integer pn, Integer ps);

    Map<String,Object> listEpisodeReplies(Integer epId, Integer pn);

    ReplyInfo getReplyInfoById(String replyId);

    Map<String, Object> listPageSonRepliesByParentId(String parentId, Integer pn, Integer ps);

    Long countReplysBetween(Date begin, Date end);

}
