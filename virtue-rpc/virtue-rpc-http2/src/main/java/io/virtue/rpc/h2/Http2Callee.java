package io.virtue.rpc.h2;

import io.virtue.common.url.URL;
import io.virtue.core.RemoteService;
import io.virtue.rpc.h1.support.AbstractHttpCallee;
import io.virtue.rpc.h2.config.Http2Callable;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.H2;
import static io.virtue.common.constant.Components.Protocol.H2C;
import static io.virtue.rpc.h1.support.HttpUtil.parseHeaders;
import static io.virtue.transport.http.HttpHeaderNames.CONTENT_TYPE;

/**
 * Http2 protocol callee.
 */
@Getter
@Accessors(fluent = true)
public class Http2Callee extends AbstractHttpCallee<Http2Callable> {

    public Http2Callee(Method method, RemoteService<?> remoteService) {
        super(method, remoteService, H2, Http2Callable.class);
    }

    @Override
    protected void doInit() {
        String pathAndParams = parsedAnnotation.path();
        path = URL.parsePath(pathAndParams);
        httpMethod = parsedAnnotation.method();
        ssl = parsedAnnotation.ssl();
        if (!ssl) protocol(H2C);
        addResponseHeaders(parseHeaders(parsedAnnotation.headers()));
        addResponseHeader(CONTENT_TYPE, parsedAnnotation.contentType());
    }

}
