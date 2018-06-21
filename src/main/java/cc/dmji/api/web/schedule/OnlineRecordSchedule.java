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

import java.util.Date;

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
    @Scheduled(cron = "0 55 23 * * ?")
    public void saveOnlineRecordToDb(){

        OnlineRecord record = new OnlineRecord();
        record.setCreateTime(new Date());
        record.setTotle(onlineUserService.countOnlineUser());
        record.setAnon(onlineUserService.countAnonOnlineUser());
        record.setAuth(onlineUserService.countAuthOnlineUser());
        record.setVisitCount(Long.valueOf(stringRedisTemplate.opsForValue().get(RedisKey.VISIT_COUNT_KEY)));
        logger.debug("online Record : {}", record.toString());

        // 将redis中浏览总数的删掉
        stringRedisTemplate.delete(RedisKey.VISIT_COUNT_KEY);

        OnlineRecord insert = onlineRecordService.insert(record);
        logger.debug("插入数据库后的在线记录 {}", insert.toString());
    }
}
