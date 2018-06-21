package cc.dmji.api.service.impl;

import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.service.OnlineUserRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echisan on 2018/6/20
 */
@Service
public class OnlineUserRedisServiceImpl implements OnlineUserRedisService {
    private static final Logger logger = LoggerFactory.getLogger(OnlineUserRedisServiceImpl.class);

    @Value("${dmji.online.user.expiration}")
    private Long onlineExpiration;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean insertOnlineUser(String userFlag, boolean isAuth) {
        if (isAuth) {
            return getAuthZSet().add(userFlag, System.currentTimeMillis());
        }
        return getAnonZSet().add(userFlag, System.currentTimeMillis());
    }

    @Override
    public Long countOnlineUser() {
        return countAnonOnlineUser() + countAuthOnlineUser();
    }

    @Override
    public Long countAuthOnlineUser() {
        double min = System.currentTimeMillis() - onlineExpiration * 1000;
        return getAuthZSet().count(min, System.currentTimeMillis());
    }

    @Override
    public Long countAnonOnlineUser() {
        double min = System.currentTimeMillis() - onlineExpiration * 1000;
        return getAnonZSet().count(min, System.currentTimeMillis());
    }

    @Override
    public Long countVisitors() {
        return getAnonZSet().count(0, System.currentTimeMillis()) + getAuthZSet().count(0, System.currentTimeMillis());
    }

    @Override
    public void deleteExpirationUsers() {
        getAnonZSet().removeRangeByScore(0, System.currentTimeMillis() - onlineExpiration * 1000);
        getAuthZSet().removeRangeByScore(0, System.currentTimeMillis() - onlineExpiration * 1000);
    }

    @Override
    public List<String> listAuthOnlineUserIds() {
        return new ArrayList<>();
    }

    // 获取认证用户的zset
    private BoundZSetOperations<String, String> getAuthZSet() {
        return stringRedisTemplate.boundZSetOps(RedisKey.ONLINE_AUTH_USER_KEY);
    }

    // 获取游客的zset
    private BoundZSetOperations<String, String> getAnonZSet() {
        return stringRedisTemplate.boundZSetOps(RedisKey.ONLINE_ANON_USER_KEY);
    }
}
