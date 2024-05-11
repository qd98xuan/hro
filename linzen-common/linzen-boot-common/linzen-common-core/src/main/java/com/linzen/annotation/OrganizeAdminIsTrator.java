package com.linzen.annotation;


import java.lang.annotation.*;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OrganizeAdminIsTrator {
    String value() default "";
}
