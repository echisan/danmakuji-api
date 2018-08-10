package cc.dmji.api.service;

import cc.dmji.api.entity.LikeRecord;

import java.util.List;

public interface LikeRecordService {

    LikeRecord insertLikeRecord(LikeRecord likeRecord);

    LikeRecord updateLikeRecord(LikeRecord likeRecord);

    LikeRecord getByReplyIdAndUserId(Long replyId, Long userId);

    List<LikeRecord> listByReplyIdsAndUserId(List<Long> replyList, Long userId);

}
