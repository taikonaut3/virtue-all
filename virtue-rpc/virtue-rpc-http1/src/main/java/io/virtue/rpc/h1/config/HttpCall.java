package io.virtue.rpc.h1.config;

import io.virtue.common.constant.Components;
import io.virtue.core.annotation.Protocol;
import io.virtue.transport.http.HttpMethod;

import java.lang.annotation.*;

/**
 * Annotation used to define an HTTP call.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Protocol(Components.Protocol.HTTP)
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

