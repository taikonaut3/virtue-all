package io.virtue.rpc.http1_1.config;

import io.virtue.core.annotation.Config;
import io.virtue.core.annotation.Options;
import io.virtue.core.annotation.CallerFactoryProvider;
import io.virtue.common.constant.Components;

import java.lang.annotation.*;

/**
 * Annotation used to define an HTTP call.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CallerFactoryProvider(Components.Protocol.HTTP)
public @interface HttpCall {

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

    Options options() default @Options;

    Config config() default @Config;
}

