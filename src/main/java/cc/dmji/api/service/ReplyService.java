package cc.dmji.api.service;

import cc.dmji.api.entity.Reply;
import cc.dmji.api.enums.Status;
import cc.dmji.api.web.model.Replies;
import cc.dmji.api.web.model.ReplyInfo;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReplyService {

    Reply insertReply(Reply reply);

    Reply updateReply(Reply reply);

    Reply deleteReply(Reply reply);

    List<Reply> listReplyByEpId(Integer epId);

    Reply getReplyById(String id);

    Long countReplyByEpId(Integer epId);

    Long countParentReplyByEpId(Integer epId);

    Page<Reply> listReplyByEpId(Integer epId, Integer page, Integer size);

    List<ReplyInfo> listSonRepliesByParentId(String parentId, String userId, Integer pn, Integer ps);


    /**
     * 获取指定集数下的评论
     *
     * @param epId 集数id
     * @param pn   页数
     * @param ps   每页获取的大小
     * @return
     */
    List<Replies> listEpisodeReplies(Integer epId, String userId, Integer pn, Integer ps);

    Map<String, Object> listEpisodeReplies(String userId, Integer epId, Integer pn);

    ReplyInfo getReplyInfoById(String replyId);

    Map<String, Object> listPageSonRepliesByParentId(String parentId, String userId, Integer pn, Integer ps);

    Long countReplysBetween(Date begin, Date end);

    List<Replies> listEpisodeRepliesByEpIdAndUserId(Integer epId,String userId,Integer pn, Integer ps);

    Map<String,Long> countByReplyIds(List<String> replyIds);

    Long countFloorByEpId(Integer epId);

    Long countFloorBetweenByEpId(Integer epId, Long begin,Long end);

    Long countSonRepliesByParentId(String parentId);

    Long countByParentIdAndCreateTimeBetween(String parentId, Date begin, Date end);

    Reply getLatestSonReplyByParentId(String parentId);

    Reply getFirstSonReplyByParentId(String parentId);

    Reply getLatestReplyByEpId(Integer epId);

    Reply getFirstReplyByEpid(Integer epId);

    Long countByEpIdAndCreateTimeBetween(Integer epId, Date begin, Date end);

    List<ReplyInfo> listSonReplyInfoByParentIds(List<String> parentIds);

    List<ReplyInfo> addReplyInfoLikeStatus(List<ReplyInfo> replyInfos, String userId);

}
