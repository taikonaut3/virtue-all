package io.virtue.rpc.h2.config;

import io.virtue.core.annotation.Protocol;
import io.virtue.transport.http.HttpMethod;

import java.lang.annotation.*;

import static io.virtue.common.constant.Components.Protocol.HTTP2;

/**
 * Use http2 protocol to make RPC call.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Protocol(HTTP2)
public @interface Http2Call {

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
     * The content type of the HTTP.
     */
    String contentType() default "application/json";

    /**
     * Whether to use SSL.
     */
    boolean ssl() default true;

}
