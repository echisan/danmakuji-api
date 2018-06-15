package cc.dmji.api.service.impl;

import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.service.RedisTokenService;
import cc.dmji.api.web.model.admin.LoginUserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by echisan on 2018/5/21
 */
@Service
public class RedisTokenServiceImpl implements RedisTokenService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveToken(String token) {
        stringRedisTemplate.opsForSet().add(RedisKey.LOGIN_TOKEN_KEY, token);
    }

    @Override
    public boolean hasToken(String token) {
        return stringRedisTemplate.opsForSet().isMember(RedisKey.LOGIN_TOKEN_KEY, token);
    }

    @Override
    public Long invalidToken(String token) {

        BoundSetOperations<String, String> setOperations = stringRedisTemplate.boundSetOps(RedisKey.LOGIN_TOKEN_KEY);
        if (setOperations.isMember(token)) {
            return stringRedisTemplate.opsForSet().remove(RedisKey.LOGIN_TOKEN_KEY, token);
        }
        return -1L;
    }

    @Override
    public List<String> listTokens(Integer pn, Integer ps) {

        // pn 当前页
        // ps 每一页的大小
        Integer limit_pn = pn == 1 ? 0 : (pn - 1) * ps;
        Integer limit_ps = pn * ps;

        SortQueryBuilder<String> builder = SortQueryBuilder.sort(RedisKey.LOGIN_TOKEN_KEY);
        builder.noSort().alphabetical(true).limit(limit_pn, limit_ps);
        return stringRedisTemplate.sort(builder.build());
    }

    @Override
    public List<LoginUserToken> listUserTokens(String username) {
        return null;
    }

    @Override
    public Long countTokens() {
        return stringRedisTemplate.opsForSet().size(RedisKey.LOGIN_TOKEN_KEY);
    }

    @Override
    public void addUserLock(String uid) {
        BoundSetOperations<String, String> boundSetOps = stringRedisTemplate.boundSetOps(RedisKey.LOGIN_LOCK_USER_KEY);
        boundSetOps.add(uid);
    }

    @Override
    public Long deleteUserLock(String uid) {
        BoundSetOperations<String, String> boundSetOps = stringRedisTemplate.boundSetOps(RedisKey.LOGIN_LOCK_USER_KEY);
        return boundSetOps.remove(uid);
    }

    @Override
    public boolean isUserLock(String uid) {
        BoundSetOperations<String, String> boundSetOps = stringRedisTemplate.boundSetOps(RedisKey.LOGIN_LOCK_USER_KEY);
        return boundSetOps.isMember(uid);
    }
}
