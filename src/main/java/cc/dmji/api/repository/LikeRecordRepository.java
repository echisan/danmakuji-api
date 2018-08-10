package cc.dmji.api.repository;

import cc.dmji.api.entity.LikeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface LikeRecordRepository extends JpaRepository<LikeRecord, Long> {

    LikeRecord getByReplyIdAndUserId(Long replyId, Long userId);

    List<LikeRecord> getByUserIdAndReplyIdIn(Long userId, Collection<Long> replyIds);
}
