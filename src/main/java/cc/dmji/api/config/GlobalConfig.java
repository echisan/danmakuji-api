package cc.dmji.api.config;

import cn.echisan.wbp4j.WbpUpload;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

import java.io.IOException;

/**
 * Created by echisan on 2018/6/15
 */
@Configuration
@EnableScheduling
@EnableAsync
public class GlobalConfig {

    @Bean
    public WbpUpload wbpUpload() throws IOException {
        //"13711511647","qq1157039476"
        return WbpUpload.builder()
                .setSinaAccount("13711511647","qq1157039476")
                .build();
    }
}
