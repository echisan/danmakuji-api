package cc.dmji.api.repository;

import cc.dmji.api.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {

    Page<Message> findByUserIdEquals(String userId, Pageable pageable);
}
