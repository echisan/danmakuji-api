package cc.dmji.api.repository;

import cc.dmji.api.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {

    Page<Message> findByUserIdEquals(String userId, Pageable pageable);

    Long countByUserIdEqualsAndTypeEqualsAndIsRead(String userId, String type, Byte isRead);

    Page<Message> findByUserIdEqualsAndTypeEquals(String userId, String type, Pageable pageable);

    Page<Message> findByUserIdEqualsAndTypeEqualsOrUserIdIsNullAndCreateTimeAfter(String userId, String type, Timestamp createTime,Pageable pageable);

    List<Message> findByUserIdEqualsAndIsReadEqualsAndTypeEquals(String userId, Byte isRead,String type);

    void deleteByIdIn(Collection<String> ids);
}
