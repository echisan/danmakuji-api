package cc.dmji.api.service.impl;

import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.service.RedisTokenService;
import cc.dmji.api.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by echisan on 2018/5/21
 */
@Service
public class RedisTokenServiceImpl implements RedisTokenService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveToken(String token) {
        stringRedisTemplate.opsForSet().add(RedisKey.LOGIN_TOKEN_KEY,token);
    }

    @Override
    public boolean hasToken(String token) {
        return stringRedisTemplate.opsForSet().isMember(RedisKey.LOGIN_TOKEN_KEY, token);
    }

    @Override
    public Long invalidToken(String token) {
        return stringRedisTemplate.opsForSet().remove(RedisKey.LOGIN_TOKEN_KEY, token);
    }

    @Override
    public Set<String> listTokens() {
        return stringRedisTemplate.opsForSet().members(RedisKey.LOGIN_TOKEN_KEY);
    }
}
