package cc.dmji.api.service;

import cc.dmji.api.entity.Danmaku;
import org.springframework.data.domain.Page;

public interface DanmakuService {

    Danmaku saveDanmaku(Danmaku danmaku);

    Page<Danmaku> listDanmakuById(String id, Integer max);

    Long countDanmakuByPlayer(String player);

    Danmaku getById(Long danmakuId);

    void deleteById(Long id);

}
