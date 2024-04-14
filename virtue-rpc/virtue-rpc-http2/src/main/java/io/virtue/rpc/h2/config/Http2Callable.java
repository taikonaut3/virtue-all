package io.virtue.rpc.h2.config;

import io.virtue.core.annotation.InvokerFactory;
import io.virtue.transport.http.HttpMethod;

import java.lang.annotation.*;

import static io.virtue.common.constant.Components.Protocol.HTTP2;

/**
 * Support be http2 protocol call.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@InvokerFactory(HTTP2)
public @interface Http2Callable {

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
     * The content type of the HTTP request.
     *
     * @return
     */
    String contentType() default "application/json";

    /**
     * Whether to use SSL.
     *
     * @return
     */
    boolean ssl() default true;
}
