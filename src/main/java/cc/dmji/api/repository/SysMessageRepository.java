package cc.dmji.api.repository;

import cc.dmji.api.entity.v2.SysMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysMessageRepository extends JpaRepository<SysMessage,Long> {
}
