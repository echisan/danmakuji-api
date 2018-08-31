package cc.dmji.api.config;

import cc.dmji.api.constants.RedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        StringRedisTemplate stringRedisTemplate = context.getBean(StringRedisTemplate.class);
        stringRedisTemplate.delete(RedisKey.WATCH_EPISODE_ONLINE_EACH);
        logger.info("clean watch_episode_online_each cache!!");
    }
}
