package cc.dmji.api.service.impl;

import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.Danmaku;
import cc.dmji.api.repository.DanmakuRespository;
import cc.dmji.api.service.DanmakuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DanmakuServiceImpl implements DanmakuService {

    private static final Logger logger = LoggerFactory.getLogger(DanmakuService.class);

    /**
     * key过期时间 3小时
     */
    private static final Long KEY_TIME_OUT = 3L;

    @Autowired
    private DanmakuRespository danmakuRespository;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public Danmaku saveDanmaku(Danmaku danmaku) throws Exception {

        Danmaku newDanmaku = danmakuRespository.save(danmaku);

        logger.info("插入数据库后的实体: [ {} ]", newDanmaku);
        String danmakuIdKey = RedisKey.DANMAKU_KEY + newDanmaku.getPlayer();
        if (redisTemplate.hasKey(danmakuIdKey)) {
            redisTemplate.delete(danmakuIdKey);
        }
        return newDanmaku;
    }

    @Override
    public List<Danmaku> listDanmakuById(String id, Integer max) {
        List<Danmaku> danmakuEntityList = null;
        String danmakuIdKey = RedisKey.DANMAKU_KEY + id;
        if (redisTemplate.hasKey(danmakuIdKey)) {
            logger.info("redis danmake_id_key is [{}] and limit [{}]", danmakuIdKey, max);
            danmakuEntityList = (List<Danmaku>) redisTemplate.opsForValue().get(danmakuIdKey);
        } else {
            danmakuEntityList = danmakuRespository.findDanmakusByPlayerEquals(id);
            redisTemplate.opsForValue().set(danmakuIdKey, danmakuEntityList, KEY_TIME_OUT, TimeUnit.HOURS);
        }

        if (danmakuEntityList.size() > max && danmakuEntityList.size() != 0) {
            danmakuEntityList.subList(0, max);
        }
        return danmakuEntityList;
    }

}
