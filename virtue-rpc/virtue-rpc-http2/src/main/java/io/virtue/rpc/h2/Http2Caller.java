package io.virtue.rpc.h2;

import io.virtue.common.url.URL;
import io.virtue.core.RemoteCaller;
import io.virtue.rpc.h1.support.AbstractHttpCaller;
import io.virtue.rpc.h2.config.Http2Call;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.H2;
import static io.virtue.common.constant.Components.Protocol.H2C;
import static io.virtue.rpc.h1.support.HttpUtil.parseHeaders;
import static io.virtue.transport.http.HttpHeaderNames.CONTENT_TYPE;

/**
 * Http2 protocol caller.
 */
@Getter
@Accessors(fluent = true)
public class Http2Caller extends AbstractHttpCaller<Http2Call> {

    public Http2Caller(Method method, RemoteCaller<?> remoteCaller) {
        super(method, remoteCaller, H2, Http2Call.class);
    }

    @Override
    protected void doInit() {
        String pathAndParams = parsedAnnotation.path();
        path = URL.parsePath(pathAndParams);
        queryParams = URL.parseParams(pathAndParams);
        httpMethod = parsedAnnotation.method();
        ssl = parsedAnnotation.ssl();
        if (!ssl) protocol(H2C);
        addRequestHeaders(parseHeaders(parsedAnnotation.headers()));
        addRequestHeader(CONTENT_TYPE, parsedAnnotation.contentType());
    }
}
