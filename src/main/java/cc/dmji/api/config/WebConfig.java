package cc.dmji.api.config;

import cc.dmji.api.web.interceptor.RequestLimitInterceptor;
import cc.dmji.api.web.filter.OnlineUserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Created by echisan on 2018/5/14
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<OnlineUserFilter> onlineUserFilterFilterRegistrationBean(){
        FilterRegistrationBean<OnlineUserFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setOrder(1);
        registrationBean.setFilter(onlineUserFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public OnlineUserFilter onlineUserFilter(){
        return new OnlineUserFilter();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLimitInterceptor());
    }

    @Bean
    public RequestLimitInterceptor requestLimitInterceptor(){
        return new RequestLimitInterceptor();
    }
}

