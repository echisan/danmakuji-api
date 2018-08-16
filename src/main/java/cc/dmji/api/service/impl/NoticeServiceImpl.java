package cc.dmji.api.service.impl;

import cc.dmji.api.entity.Notice;
import cc.dmji.api.repository.NoticeRepository;
import cc.dmji.api.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Created by echisan on 2018/7/22
 */
@Service
public class NoticeServiceImpl implements NoticeService {
    private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    @Transactional
    public Notice insertNotice(Notice notice) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        notice.setCreateTime(ts);
        notice.setModifyTime(ts);
        notice.setViewCount(0L);
        return noticeRepository.save(notice);
    }

    @Override
    @Transactional
    public Notice updateNotice(Notice notice) {
        notice.setModifyTime(new Timestamp(System.currentTimeMillis()));
        return noticeRepository.save(notice);
    }

    @Override
    public Page<Notice> listNotices(Integer pn, Integer ps) {
        return noticeRepository.findAll(PageRequest.of(pn-1,ps,Sort.Direction.DESC,"createTime"));
    }

    @Override
    public Notice getNoticeByShowIndex() {
        return noticeRepository.findByIsShowIndexEquals((byte)1);
    }

    @Override
    @Transactional
    public void deleteNoticeById(Long nid) {
        noticeRepository.deleteById(nid);
    }

    @Override
    public Notice getNoticeById(Long nid) {
        return noticeRepository.findById(nid).orElse(null);
    }

    @Override
    @Transactional
    public Notice updateNoticeViewCountById(Notice notice) {
        notice.setViewCount(notice.getViewCount() == null ? 1 : notice.getViewCount() + 1);
        return noticeRepository.save(notice);
    }
}
