package io.github.astro.virtue.rpc.http1.config;

import java.lang.annotation.*;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/9 13:49
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    String value() default "";
}
