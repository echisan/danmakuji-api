package cc.dmji.api.service;

import cc.dmji.api.entity.Reply;
import cc.dmji.api.enums.ReplyOrderBy;
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

    List<Reply> listReplyByEpId(Long epId);

    Reply getReplyById(Long id);

    Long countReplyByEpId(Long epId);

    Long countParentReplyByEpId(Long epId);

    Page<Reply> listReplyByEpId(Long epId, Integer page, Integer size);

    List<ReplyInfo> listSonRepliesByParentId(Long parentId, Long userId, Integer pn, Integer ps);


    /**
     * 获取指定集数下的评论
     *
     * @param epId 集数id
     * @param pn   页数
     * @param ps   每页获取的大小
     * @return
     */
    List<Replies> listEpisodeReplies(Long epId, ReplyOrderBy orderBy, Long userId, Integer pn, Integer ps);

    Map<String, Object> listEpisodeRepliesToMap(Long userId, ReplyOrderBy orderBy, Long epId, Integer pn,Integer ps);

    ReplyInfo getReplyInfoById(Long replyId);

    Map<String, Object> listPageSonRepliesByParentId(Long parentId, Long userId, Integer pn, Integer ps);

    Long countReplysBetween(Date begin, Date end);

    List<Replies> listEpisodeRepliesByEpIdAndUserId(Long epId,Long userId,Integer pn, Integer ps);

    Map<Long,Long> countByReplyIds(List<Long> replyIds);

    Long countFloorByEpId(Long epId);

    Long countFloorBetweenByEpId(Long epId, Long begin,Long end);

    Long countSonRepliesByParentId(Long parentId);

    Long countByParentIdAndCreateTimeBetween(Long parentId, Date begin, Date end);

    Reply getLatestSonReplyByParentId(Long parentId);

    Reply getFirstSonReplyByParentId(Long parentId);

    Reply getLatestReplyByEpId(Long epId);

    Reply getFirstReplyByEpid(Long epId);

    Long countByEpIdAndCreateTimeBetween(Long epId, Date begin, Date end);

    List<ReplyInfo> listSonReplyInfoByParentIds(List<Long> parentIds);

    List<ReplyInfo> addReplyInfoLikeStatus(List<ReplyInfo> replyInfos, Long userId);

}
