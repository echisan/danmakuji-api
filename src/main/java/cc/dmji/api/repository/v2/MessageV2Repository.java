package cc.dmji.api.repository.v2;

import cc.dmji.api.entity.v2.MessageV2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageV2Repository extends JpaRepository<MessageV2, Long> {
}
