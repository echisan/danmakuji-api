package cc.dmji.api.repository;

import cc.dmji.api.entity.Danmaku;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DanmakuRespository extends JpaRepository<Danmaku, Long> {

    Page<Danmaku> findDanmakuByDanmakuIdEquals(String danmakuId, Pageable pageable);

    Long countByDanmakuIdEquals(String danmakuId);
}
