package cc.dmji.api.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * Created by echisan on 2018/6/15
 */
@Configuration
@EnableScheduling
@EnableAsync
public class GlobalConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }
}
