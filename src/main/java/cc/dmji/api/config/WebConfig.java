package cc.dmji.api.config;

import cc.dmji.api.web.interceptor.ValidUserSelfInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by echisan on 2018/5/14
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(validUserSelfInterceptor());
    }

    @Bean
    public ValidUserSelfInterceptor validUserSelfInterceptor(){
        return new ValidUserSelfInterceptor();
    }
}
