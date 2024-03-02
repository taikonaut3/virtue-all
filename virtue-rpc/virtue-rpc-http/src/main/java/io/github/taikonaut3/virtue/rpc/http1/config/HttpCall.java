package io.github.taikonaut3.virtue.rpc.http1.config;

import io.github.taikonaut3.virtue.config.annotation.CallerFactoryProvider;

import java.lang.annotation.*;

import static io.github.taikonaut3.virtue.common.constant.Components.Protocol.HTTP;

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
    HttpMethod method() default HttpMethod.GET;

    /**
     * The headers of the HTTP request.
     */
    String[] headers() default {};

    /**
     * The query parameters of the HTTP request.
     */
    String[] params() default {};
}

