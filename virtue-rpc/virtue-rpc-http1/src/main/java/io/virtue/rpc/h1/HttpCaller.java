package io.virtue.rpc.h1;

import io.virtue.common.url.URL;
import io.virtue.core.RemoteCaller;
import io.virtue.rpc.h1.config.HttpCall;
import io.virtue.rpc.h1.support.AbstractHttpCaller;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.HTTP;
import static io.virtue.rpc.h1.support.HttpUtil.parseHeaders;
import static io.virtue.transport.http.HttpHeaderNames.CONTENT_TYPE;

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
        addRequestHeader(CONTENT_TYPE, parsedAnnotation.contentType());
    }
}
