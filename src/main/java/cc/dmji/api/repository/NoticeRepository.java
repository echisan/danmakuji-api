package cc.dmji.api.repository;

import cc.dmji.api.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice,Long> {

    Notice findByIsShowIndexEquals(Byte isShowIndex);
}
