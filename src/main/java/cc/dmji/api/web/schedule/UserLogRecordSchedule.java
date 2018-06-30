package cc.dmji.api.web.schedule;

import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.UserLogRecord;
import cc.dmji.api.service.UserLogRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by echisan on 2018/6/19
 */
@Component
public class UserLogRecordSchedule {
    private static final Logger logger = LoggerFactory.getLogger(UserLogRecordSchedule.class);

    @Autowired
    @Qualifier("userLogRecordServiceImpl")
    private UserLogRecordService userLogRecordService;

    @Autowired
    @Qualifier("userLogRecordRedisServiceImpl")
    private UserLogRecordService userLogRecordRedisService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 每10分钟整一遍
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void saveRedisRecordToDb() {
        List<UserLogRecord> redisUserLogRecord = userLogRecordRedisService.listUserLogRecords();
        if (redisUserLogRecord != null && redisUserLogRecord.size() != 0) {
            // 清空内存中的记录
            stringRedisTemplate.delete(RedisKey.USER_LOG_RECORD_KEY);
            logger.info("已清除redis中的用户操作记录,共清除记录{}条", redisUserLogRecord.size());
            saveUserLog(redisUserLogRecord);
        }
        logger.info("缓存中暂无用户日志记录,无需写入数据库");
    }

    @Async
    public void saveUserLog(List<UserLogRecord> userLogRecordList){
        // 插入到数据库中
        List<UserLogRecord> userLogRecords = userLogRecordService.insertUserLogRecords(userLogRecordList);
        logger.info("用户日志记录持久化成功!共读取数据{}条,写入数据{}条", userLogRecordList.size(), userLogRecords.size());
    }
}
