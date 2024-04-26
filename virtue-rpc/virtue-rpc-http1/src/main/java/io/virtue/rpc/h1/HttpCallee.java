package io.virtue.rpc.h1;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.virtue.common.url.URL;
import io.virtue.core.RemoteService;
import io.virtue.rpc.h1.config.HttpCallable;
import io.virtue.rpc.h1.support.AbstractHttpCallee;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.HTTP;
import static io.virtue.rpc.h1.support.HttpUtil.parseHeaders;

/**
 * Http1.1 protocol callee.
 */
public class HttpCallee extends AbstractHttpCallee<HttpCallable> {

    public HttpCallee(Method method, RemoteService<?> remoteService) {
        super(method, remoteService, HTTP, HttpCallable.class);
    }

    @Override
    protected void doInit() {
        String pathAndParams = parsedAnnotation.path();
        path = URL.parsePath(pathAndParams);
        httpMethod = parsedAnnotation.method();
        addResponseHeaders(parseHeaders(parsedAnnotation.headers()));
        addResponseHeader(HttpHeaderNames.CONTENT_TYPE, parsedAnnotation.contentType());
    }
}
