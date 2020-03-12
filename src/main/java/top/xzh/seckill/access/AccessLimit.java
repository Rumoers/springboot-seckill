package top.xzh.seckill.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 访问限制, 通过拦截器实现.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    /** 几秒内 */
    int seconds();
    /** 最多允许几次访问 */
    int maxCount();
    /** 默认需要登录 */
    boolean needLogin() default true;
}
