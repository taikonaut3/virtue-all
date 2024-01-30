package io.github.astro.virtue.rpc.http1_1.config;

import io.github.astro.virtue.config.annotation.BindingCaller;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.rpc.http1_1.HttpServerCaller;

import java.lang.annotation.*;

import static io.github.astro.virtue.common.constant.Components.Protocol.HTTP;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/9 13:19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BindingCaller(protocol = HTTP, serverCaller = HttpServerCaller.class)
public @interface HttpCallable {

    /**
     * The path of the HTTP request.
     */
    String path() default "";

    /**
     * The method of the HTTP request.
     */
    String method() default "";

    /**
     * The headers of the HTTP request.
     */
    String[] headers() default {};

    /**
     * The query parameters of the HTTP request.
     */
    String[] params() default {};

    Config config() default @Config;
}
