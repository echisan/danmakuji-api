package cc.dmji.api.repository;

import cc.dmji.api.entity.Danmaku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DanmakuRespository extends JpaRepository<Danmaku, Long> {

    List<Danmaku> findDanmakusByPlayerEquals(String player);
}
