package cc.dmji.api.repository.v2;

import cc.dmji.api.entity.v2.MessageV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageV2Repository extends JpaRepository<MessageV2, Long> {
    List<MessageV2> findByUidEqualsAndTypeEqualsAndReadEquals(Long uid, Integer messageType, boolean isRead);

    List<MessageV2> findByUidEqualsAndReadEquals(Long uid, boolean isRead);

    Long countByUidEqualsAndTypeEqualsAndReadEquals(Long uid, Integer messageType, boolean isRead);

    List<MessageV2> findByIdIn(List<Long> ids);

}
