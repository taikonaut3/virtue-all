package io.virtue.rpc.h2;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.virtue.common.constant.Key;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.Parameterization;
import io.virtue.common.util.StringUtil;
import io.virtue.rpc.h2.config.Http2Call;
import io.virtue.rpc.h2.config.Http2Callable;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.util.TransportUtil;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.virtue.common.constant.Components.Protocol.H2;
import static io.virtue.common.constant.Components.Protocol.H2C;

/**
 * HTTP 2 generic information encapsulation class.
 */
@Getter
@Accessors(fluent = true)
public class Http2Wrapper implements Parameterization {

    private final String path;
    @Parameter(Key.HTTP_METHOD)
    private final HttpMethod httpMethod;
    @Parameter(Key.SSL)
    private final boolean ssl;
    private final Map<CharSequence, CharSequence> params;
    private final Map<CharSequence, CharSequence> headers;

    public Http2Wrapper(Http2Call call, Http2Caller caller) {
        String pathAndParams = call.path();
        ssl = call.ssl();
        caller.protocol(ssl ? H2 : H2C);
        path = TransportUtil.parsePath(pathAndParams);
        httpMethod = call.method();
        headers = new LinkedHashMap<>();
        addHeaders(HttpUtil.commonClientHeaders());
        addHeaders(HttpUtil.parseHeaders(call.headers()));
        addHeader(HttpHeaderNames.CONTENT_TYPE, call.contentType());
        params = TransportUtil.parseParams(pathAndParams);
    }

    public Http2Wrapper(Http2Callable callable, Http2Callee callee) {
        String pathAndParams = callable.path();
        ssl = callable.ssl();
        callee.protocol(ssl ? H2 : H2C);
        path = TransportUtil.parsePath(pathAndParams);
        httpMethod = callable.method();
        headers = new LinkedHashMap<>();
        addHeaders(HttpUtil.commonServerHeaders());
        addHeaders(HttpUtil.parseHeaders(callable.headers()));
        addHeader(HttpHeaderNames.CONTENT_TYPE, callable.contentType());
        params = TransportUtil.parseParams(pathAndParams);
    }

    private void addHeaders(Map<CharSequence, CharSequence> headers) {
        headers.forEach(this::addHeader);
    }

    private void addHeader(CharSequence key, CharSequence value) {
        if (!StringUtil.isBlank(key)) {
            headers.put(key, value);
        }
    }
}
