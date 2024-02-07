package io.github.astro.virtue.rpc.http1_1.config;

import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.Options;
import io.github.astro.virtue.config.annotation.CallerFactoryProvider;
import io.github.astro.virtue.rpc.http1_1.HttpCallerFactory;

import java.lang.annotation.*;

import static io.github.astro.virtue.common.constant.Components.Protocol.HTTP;

/**
 * Annotation used to define an HTTP call.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CallerFactoryProvider(HTTP)
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

