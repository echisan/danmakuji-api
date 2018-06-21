package cc.dmji.api.repository;

import cc.dmji.api.entity.UserLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserLogRecordRepository extends JpaRepository<UserLogRecord,Long> {

    @Query(value = "select * from dm_user_log_record as u where u.user_role = ?1 order by u.create_time desc limit ?2,?3", nativeQuery = true)
    List<UserLogRecord> listUserLogRecords(String userRole, Integer pn, Integer ps);

    @Query(value = "select * from dm_user_log_record as u order by u.create_time desc limit ?1,?2",nativeQuery = true)
    List<UserLogRecord> listUserLogRecords(Integer pn, Integer ps);

}
