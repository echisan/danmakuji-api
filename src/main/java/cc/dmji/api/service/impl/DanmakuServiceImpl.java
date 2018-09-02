package cc.dmji.api.service.impl;

import cc.dmji.api.entity.Danmaku;
import cc.dmji.api.repository.DanmakuRespository;
import cc.dmji.api.service.DanmakuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DanmakuServiceImpl implements DanmakuService {

    private static final Logger logger = LoggerFactory.getLogger(DanmakuService.class);

    @Autowired
    private DanmakuRespository danmakuRespository;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    @Transactional
    public Danmaku saveDanmaku(Danmaku danmaku) {
        return danmakuRespository.save(danmaku);
    }

    @Override
    public Page<Danmaku> listDanmakuById(String danmakuId, Integer max) {
        return danmakuRespository.findDanmakuByDanmakuIdEquals(danmakuId, PageRequest.of(0, max));
    }

    @Override
    public Long countDanmakuByPlayer(String danmakuId) {
        return danmakuRespository.countByDanmakuIdEquals(danmakuId);
    }
}
