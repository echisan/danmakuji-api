package cc.dmji.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@EnableCaching
@Configuration
public class CacheConfig extends CachingConfigurerSupport {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName()).append("#");
            sb.append(method.getName()).append("(");
            for (Object obj : params) {
                sb.append(obj.toString()).append(",");
            }
            sb.append(")");
            return sb.toString();
        };
    }
}
