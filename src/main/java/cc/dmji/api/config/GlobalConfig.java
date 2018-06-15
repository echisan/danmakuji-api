package cc.dmji.api.config;

import cn.echisan.wbp4j.WbpUpload;
import cn.echisan.wbp4j.WbpUploadBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by echisan on 2018/6/15
 */
@Configuration
public class GlobalConfig {

    @Bean
    public WbpUpload wbpUpload(){
        return new WbpUploadBuilder()
                .setAccount("13711511647","qq1157039476")
                .setDev(true)
                .build();
    }
}
