package cc.dmji.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserLog {

    /**
     * 该方法的描述，加入访问了 POST /users
     * 应该写的是 注册账号
     * @return 该方法的描述
     */
    String value() default "";
}
