package cc.dmji.api.web.schedule;

import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.OnlineRecord;
import cc.dmji.api.service.OnlineRecordService;
import cc.dmji.api.service.OnlineUserRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by echisan on 2018/6/20
 */
@Component
public class OnlineRecordSchedule {
    private static final Logger logger = LoggerFactory.getLogger(OnlineRecordSchedule.class);

    @Autowired
    private OnlineUserRedisService onlineUserService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OnlineRecordService onlineRecordService;

    // 应该是每天晚上11点55分操作一遍
    @Scheduled(cron = "0 58 23 * * ?")
    public void saveOnlineRecordToDb() {

        OnlineRecord record = new OnlineRecord();
        record.setCreateTime(new Date());
        Long totalAuth = onlineUserService.countTodayTotalAuthOnlineUser();
        Long totalAnon = onlineUserService.countTodayTotalAnonOnlineUser();

        record.setTotal(totalAnon+totalAuth);
        record.setAnon(totalAnon);
        record.setAuth(totalAuth);
        record.setApiCount(Long.valueOf(stringRedisTemplate.opsForValue().get(RedisKey.VISIT_COUNT_KEY)));
        record.setTotalVisitors(onlineUserService.countVisitors());
        record.setMaxAnon(onlineUserService.countTodayMaxAnonOnlineUser());
        record.setMaxAuth(onlineUserService.countTodayMaxAuthOnlineUser());
        record.setMaxTotal(onlineUserService.countTodayMaxOnlineUser());
        logger.debug("online Record : {}", record.toString());

        // 将一些redis中的key删掉，重新计算
        List<String> keys = new ArrayList<>();
        keys.add(RedisKey.VISIT_COUNT_KEY);
        keys.add(RedisKey.MAX_ONLINE_ANON_USER_KEY);
        keys.add(RedisKey.MAX_ONLINE_AUTH_USER_KEY);
        keys.add(RedisKey.MAX_ONLINE_TOTAL_USER_KEY);
        keys.add(RedisKey.ONLINE_ANON_USER_KEY);
        keys.add(RedisKey.ONLINE_AUTH_USER_KEY);

        stringRedisTemplate.delete(keys);

        OnlineRecord insert = onlineRecordService.insert(record);
        logger.debug("插入数据库后的在线记录 {}", insert.toString());
    }

    // 每5分钟记录一遍人数
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void recordMaxOnlineUsers() {
        // 统计在线游客数峰值
        Long maxAnon = onlineUserService.countTodayMaxAnonOnlineUser();
        Long nowAnon = onlineUserService.countAnonOnlineUser();
        if (nowAnon > maxAnon) {
            maxAnon = nowAnon;
            stringRedisTemplate.opsForValue().set(RedisKey.MAX_ONLINE_ANON_USER_KEY, maxAnon.toString());
        }

        Long maxAuth = onlineUserService.countTodayMaxAuthOnlineUser();
        Long nowAuth = onlineUserService.countAuthOnlineUser();
        if (nowAuth > maxAuth) {
            maxAuth = nowAuth;
            stringRedisTemplate.opsForValue().set(RedisKey.MAX_ONLINE_AUTH_USER_KEY, maxAuth.toString());
        }

        Long maxTotal = onlineUserService.countTodayMaxOnlineUser();
        Long nowTotal = nowAnon + nowAuth;
        if (nowTotal > maxTotal) {
            maxTotal = nowTotal;
            stringRedisTemplate.opsForValue().set(RedisKey.MAX_ONLINE_TOTAL_USER_KEY, maxTotal.toString());
        }

        logger.debug("当前在线游客:{},当前在线注册用户:{}", nowAnon, nowAuth);
        logger.debug("今日在线游客峰值:{},在线注册用户峰值:{},今日同时在线峰值:{}", maxAnon, maxAuth, maxTotal);
    }
}
