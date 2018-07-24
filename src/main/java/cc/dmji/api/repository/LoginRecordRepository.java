package cc.dmji.api.repository;

import cc.dmji.api.entity.LoginRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginRecordRepository extends JpaRepository<LoginRecord,Long> {

    List<LoginRecord> findLoginRecordsByIpEquals(String ip);

    List<LoginRecord> findLoginRecordsByUserIdEquals(Long userId);

    List<LoginRecord> findLoginRecordsByUserIdEqualsAndIpEquals(Long userId, String ip);
}
