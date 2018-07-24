package cc.dmji.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

/**
 * Created by echisan on 2018/6/15
 */
@Configuration
@EnableScheduling
@EnableAsync
public class GlobalConfig {
}
