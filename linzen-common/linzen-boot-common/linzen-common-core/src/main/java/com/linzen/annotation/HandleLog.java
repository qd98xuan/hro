package com.linzen.annotation;

import java.lang.annotation.*;

/**
 * 请求日志注解
 *
 * @author FHNP
 * @version: V3.1.0
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandleLog {

    /**
     * 操作模块
     *
     * @return
     */
    String moduleName() default "";

    /**
     * 操作方式
     *
     * @return
     */
    String requestMethod() default "";

}
