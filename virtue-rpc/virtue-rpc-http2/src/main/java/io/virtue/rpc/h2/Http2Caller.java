package io.virtue.rpc.h2;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.virtue.common.constant.Key;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.URL;
import io.virtue.core.RemoteCaller;
import io.virtue.rpc.h1.AbstractHttpCaller;
import io.virtue.rpc.h2.config.Http2Call;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.*;
import static io.virtue.rpc.h1.HttpUtil.parseHeaders;

/**
 * Http2 protocol caller.
 */
@Getter
@Accessors(fluent = true)
public class Http2Caller extends AbstractHttpCaller<Http2Call> {

    @Parameter(Key.SSL)
    private boolean ssl;

    public Http2Caller(Method method, RemoteCaller<?> remoteCaller) {
        super(method, remoteCaller, HTTP2, Http2Call.class);
    }

    @Override
    protected void doInit() {
        String pathAndParams = parsedAnnotation.path();
        path = URL.parsePath(pathAndParams);
        queryParams = URL.parseParams(pathAndParams);
        httpMethod = parsedAnnotation.method();
        ssl = parsedAnnotation.ssl();
        protocol(ssl ? H2 : H2C);
        addRequestHeaders(parseHeaders(parsedAnnotation.headers()));
        addRequestHeader(HttpHeaderNames.CONTENT_TYPE, parsedAnnotation.contentType());
    }
}
