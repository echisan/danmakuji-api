package cc.dmji.api.service.impl;

import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.UserLogRecord;
import cc.dmji.api.enums.Role;
import cc.dmji.api.service.UserLogRecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by echisan on 2018/6/19
 */
@Service
public class UserLogRecordRedisServiceImpl implements UserLogRecordService {
    private static final Logger logger = LoggerFactory.getLogger(UserLogRecordServiceImpl.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public UserLogRecord insertUserLogRecord(UserLogRecord userLogRecord) {
        long index = 0L;
        try {
            String jsonUserLog = new ObjectMapper().writeValueAsString(userLogRecord);
            index = getListOps().rightPush(jsonUserLog);
            logger.debug("用户日志记录存入成功,index[{}]",index);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        userLogRecord.setId(index);
        return userLogRecord;
    }

    @Override
    @Deprecated
    public void deleteUserLogRecordById(Long id) {
        logger.info("该类[{}],不支持本方法[{}]",getClass().getName(),"deleteUserLogRecordById(Long id)");
    }

    @Override
    @Deprecated
    public void deleteUserLogRecordByIds(List<UserLogRecord> userLogRecords) {
        logger.info("该类[{}],不支持本方法[{}]",getClass().getName(),"deleteUserLogRecordByIds(List<UserLogRecord> userLogRecords)");
    }

    @Override
    @Deprecated
    public UserLogRecord updateUserLogRecord(UserLogRecord userLogRecord) {
        logger.info("该类[{}],不支持本方法[{}]",getClass().getName(),"updateUserLogRecord(UserLogRecord userLogRecord)");
        return null;
    }

    @Override
    @Deprecated
    public UserLogRecord getUserLogRecordById(Long id) {
        logger.info("该类[{}],不支持本方法[{}]",getClass().getName(),"getUserLogRecordById(Long id)");
        return null;
    }

    @Override
    public List<UserLogRecord> listUserLogRecords(Role role, Integer pn, Integer ps){
        logger.warn("由于用户日志写入十分频繁，因此分页功能可能不太奏效");
        long start = pn == 1 ? 0 : (pn - 1) * ps;
        long end = pn * ps;
        List<String> jsonUserLog = getListOps().range(start, end);
        List<UserLogRecord> userLogRecords = new ArrayList<>(jsonUserLog.size());
        jsonUserLog.forEach(s -> {
            try {
                userLogRecords.add(new ObjectMapper().readValue(s, UserLogRecord.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return userLogRecords;
    }

    @Override
    public List<UserLogRecord> listUserLogRecords() {
        List<String> jsonLogRecord = getListOps().range(0, -1);
        List<UserLogRecord> userLogRecords = new ArrayList<>(jsonLogRecord.size());
        jsonLogRecord.forEach(s -> {
            try {
                userLogRecords.add(new ObjectMapper().readValue(s, UserLogRecord.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return userLogRecords;
    }

    @Override
    @Deprecated
    public List<UserLogRecord> insertUserLogRecords(List<UserLogRecord> userLogRecords) {
        logger.info("该类[{}],不支持本方法[{}]",getClass().getName(),"insertUserLogRecords(List<UserLogRecord> userLogRecords)");
        return null;
    }

    private BoundListOperations<String, String> getListOps() {
        return stringRedisTemplate.boundListOps(RedisKey.USER_LOG_RECORD_KEY);
    }
}
