package cc.dmji.api.repository;

import cc.dmji.api.entity.LoginRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginRecordRepository extends JpaRepository<LoginRecord,String> {

    List<LoginRecord> findLoginRecordsByIpEquals(String ip);

    List<LoginRecord> findLoginRecordsByUserIdEquals(String userId);

    List<LoginRecord> findLoginRecordsByUserIdEqualsAndIpEquals(String userId, String ip);
}
