package cc.dmji.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 限制用户的请求频率
 * 只能限制同一个用户或同一个ip地址访问同一个方法的频率
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {

    /**
     * 请求频繁时的提示，如果为空则采用ResultCode中的msg
     */
    String value() default "";

    /**
     * 请求时间显示，默认为3秒，
     */
    String timeout() default "3";

    /**
     * 请求限制的时间单位，与timeout配合使用
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
