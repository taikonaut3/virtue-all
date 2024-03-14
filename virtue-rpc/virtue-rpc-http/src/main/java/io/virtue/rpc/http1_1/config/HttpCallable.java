package io.virtue.rpc.http1_1.config;

import io.virtue.config.annotation.CallerFactoryProvider;
import io.virtue.config.annotation.Config;
import io.virtue.common.constant.Components;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CallerFactoryProvider(Components.Protocol.HTTP)
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
