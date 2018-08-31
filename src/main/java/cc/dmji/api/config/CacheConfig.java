package cc.dmji.api.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

//@EnableCaching
//@Configuration
public class CacheConfig extends CachingConfigurerSupport {

//    @Bean
//    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//        return RedisCacheManager.create(connectionFactory);
//    }


//    @Bean
//    public KeyGenerator wiselyKeyGenerator() {
//        return (target, method, params) -> {
//            StringBuilder sb = new StringBuilder();
//            sb.append(target.getClass().getName()).append("#");
//            sb.append(method.getName()).append("(");
//            for (Object obj : params) {
//                sb.append(obj.toString()).append(",");
//            }
//            sb.append(")");
//            return sb.toString();
//        };
//    }

//    @Bean
//    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory){
//        @SuppressWarnings({"rawtypes","unchecked"})
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(factory);
//        stringRedisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
//        stringRedisTemplate.afterPropertiesSet();
//        return stringRedisTemplate;
//    }
}
