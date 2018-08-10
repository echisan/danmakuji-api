package cc.dmji.api.repository;

import cc.dmji.api.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByUserIdEquals(Long userId, Pageable pageable);

    Long countByUserIdEqualsAndTypeEqualsAndIsRead(Long userId, String type, Byte isRead);

    Page<Message> findByUserIdEqualsAndTypeEquals(Long userId, String type, Pageable pageable);

    Page<Message> findByUserIdEqualsAndTypeEqualsOrUserIdIsNullAndCreateTimeAfter(Long userId, String type, Timestamp createTime,Pageable pageable);

    List<Message> findByUserIdEqualsAndIsReadEqualsAndTypeEquals(Long userId, Byte isRead,String type);

    void deleteByIdIn(Collection<Long> ids);
}
