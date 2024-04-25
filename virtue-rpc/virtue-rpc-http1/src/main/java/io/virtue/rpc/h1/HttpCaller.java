package io.virtue.rpc.h1;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.virtue.common.url.URL;
import io.virtue.core.RemoteCaller;
import io.virtue.rpc.h1.config.HttpCall;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.HTTP;
import static io.virtue.rpc.h1.HttpUtil.parseHeaders;

/**
 * Http1.1 protocol caller.
 */
public class HttpCaller extends AbstractHttpCaller<HttpCall> {

    public HttpCaller(Method method, RemoteCaller<?> remoteCaller) {
        super(method, remoteCaller, HTTP, HttpCall.class);
    }

    @Override
    protected void doInit() {
        String pathAndParams = parsedAnnotation.path();
        path = URL.parsePath(pathAndParams);
        queryParams = URL.parseParams(pathAndParams);
        httpMethod = parsedAnnotation.method();
        addRequestHeaders(parseHeaders(parsedAnnotation.headers()));
        addRequestHeader(HttpHeaderNames.CONTENT_TYPE, parsedAnnotation.contentType());
    }
}
