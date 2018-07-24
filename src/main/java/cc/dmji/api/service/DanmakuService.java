package cc.dmji.api.service;

import cc.dmji.api.entity.Danmaku;

import java.util.List;

public interface DanmakuService {

    Danmaku saveDanmaku(Danmaku danmaku) throws Exception;
    List<Danmaku> listDanmakuById(String id, Integer max) throws Exception;
    Long countDanmakuByPlayer(String player);

}
