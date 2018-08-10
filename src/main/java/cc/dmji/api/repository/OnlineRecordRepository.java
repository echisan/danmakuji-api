package cc.dmji.api.repository;

import cc.dmji.api.entity.OnlineRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface OnlineRecordRepository extends JpaRepository<OnlineRecord, Long> {

    List<OnlineRecord> findOnlineRecordsByCreateTimeBetween(Date begin, Date end);
}
