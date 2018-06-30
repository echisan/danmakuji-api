package cc.dmji.api.service;

import cc.dmji.api.entity.LikeRecord;

import java.util.List;

public interface LikeRecordService {

    LikeRecord insertLikeRecord(LikeRecord likeRecord);

    LikeRecord updateLikeRecord(LikeRecord likeRecord);

    LikeRecord getByReplyIdAndUserId(String replyId, String userId);

    List<LikeRecord> listByReplyIdsAndUserId(List<String> replyList, String userId);

}
