package cc.dmji.api.service;

import cc.dmji.api.entity.Notice;
import org.springframework.data.domain.Page;

public interface NoticeService {

    Notice insertNotice(Notice notice);

    Notice updateNotice(Notice notice);

    Page<Notice> listNotices(Integer pn, Integer ps);

    Notice getNoticeByShowIndex();

    void deleteNoticeById(Long nid);

    Notice getNoticeById(Long nid);

    Notice updateNoticeViewCountById(Notice notice);

}
