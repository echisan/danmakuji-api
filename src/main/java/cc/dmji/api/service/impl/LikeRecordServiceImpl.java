package cc.dmji.api.service.impl;

import cc.dmji.api.entity.LikeRecord;
import cc.dmji.api.repository.LikeRecordRepository;
import cc.dmji.api.service.LikeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by echisan on 2018/6/24
 */
@Service
public class LikeRecordServiceImpl implements LikeRecordService {

    @Autowired
    private LikeRecordRepository likeRecordRepository;

    @Override
    public LikeRecord insertLikeRecord(LikeRecord likeRecord) {
        likeRecord.setCreateTime(new Date());
        likeRecord.setModifyTime(new Date());
        return likeRecordRepository.save(likeRecord);
    }

    @Override
    public LikeRecord updateLikeRecord(LikeRecord likeRecord) {
        likeRecord.setModifyTime(new Date());
        return likeRecordRepository.save(likeRecord);
    }

    @Override
    public LikeRecord getByReplyIdAndUserId(Long replyId, Long userId) {
        return likeRecordRepository.getByReplyIdAndUserId(replyId, userId);
    }

    @Override
    public List<LikeRecord> listByReplyIdsAndUserId(List<Long> replyList, Long userId) {
        return likeRecordRepository.getByUserIdAndReplyIdIn(userId, replyList);
    }
}
