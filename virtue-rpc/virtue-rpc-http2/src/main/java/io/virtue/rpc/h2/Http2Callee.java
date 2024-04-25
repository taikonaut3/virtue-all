package io.virtue.rpc.h2;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteService;
import io.virtue.rpc.h1.AbstractHttpCallee;
import io.virtue.rpc.h2.config.Http2Callable;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.*;
import static io.virtue.rpc.h1.HttpUtil.parseHeaders;

/**
 * Http2 protocol callee.
 */
@Getter
@Accessors(fluent = true)
public class Http2Callee extends AbstractHttpCallee<Http2Callable> {

    @Parameter(Key.SSL)
    private  boolean ssl;

    public Http2Callee(Method method, RemoteService<?> remoteService) {
        super(method, remoteService, HTTP2, Http2Callable.class);
    }

    @Override
    protected void doInit() {
        String pathAndParams = parsedAnnotation.path();
        path = URL.parsePath(pathAndParams);
        httpMethod = parsedAnnotation.method();
        ssl = parsedAnnotation.ssl();
        protocol(ssl ? H2 : H2C);
        addResponseHeaders(parseHeaders(parsedAnnotation.headers()));
        addResponseHeader(HttpHeaderNames.CONTENT_TYPE, parsedAnnotation.contentType());
    }


    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        Object result = doInvoke(invocation);
        sendResponse(invocation, result);
        if (result instanceof Exception e) {
            throw RpcException.unwrap(e);
        }
        return result;
    }
}
