package io.virtue.rpc.h2;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.Parameterization;
import io.virtue.common.util.StringUtil;
import io.virtue.rpc.h2.config.Http2Call;
import io.virtue.rpc.h2.config.Http2Callable;
import io.virtue.transport.http.HttpHeaderNames;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.http.h2.Http2StreamSender;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP 2 generic information encapsulation class.
 */
@Getter
@Accessors(fluent = true)
public class Http2Invoker implements Parameterization {

    private final String path;
    @Parameter(Key.HTTP_METHOD)
    private final HttpMethod httpMethod;
    @Parameter(Key.SSL)
    private final boolean ssl;
    private final Http2StreamSender sender;
    private final Map<CharSequence, CharSequence> params;
    private final Map<CharSequence, CharSequence> headers;

    public Http2Invoker(Http2Call call, Http2Caller caller) {
        String pathAndParams = call.path();
        ssl = call.ssl();
        caller.protocol(ssl ? "h2" : "h2c");
        path = HttpUtil.parsePath(pathAndParams);
        httpMethod = call.method();
        headers = new LinkedHashMap<>();
        addHeaders(HttpUtil.commonClientHeaders());
        addHeaders(HttpUtil.parseHeaders(call.headers()));
        addHeader(HttpHeaderNames.CONTENT_TYPE, call.contentType());
        params = HttpUtil.parseParams(pathAndParams);
        String transport = caller.remoteCaller().virtue().configManager().applicationConfig().transport();
        transport = StringUtil.isBlankOrDefault(transport, Constant.DEFAULT_TRANSPORTER);
        sender = ExtensionLoader.loadExtension(Http2StreamSender.class, transport);
    }

    public Http2Invoker(Http2Callable callable, Http2Callee callee) {
        String pathAndParams = callable.path();
        ssl = callable.ssl();
        callee.protocol(ssl ? "h2" : "h2c");
        path = HttpUtil.parsePath(pathAndParams);
        httpMethod = callable.method();
        headers = new LinkedHashMap<>();
        addHeaders(HttpUtil.commonClientHeaders());
        addHeaders(HttpUtil.parseHeaders(callable.headers()));
        addHeader(HttpHeaderNames.CONTENT_TYPE, callable.contentType());
        params = HttpUtil.parseParams(pathAndParams);
        String transport = callee.remoteService().virtue().configManager().applicationConfig().transport();
        transport = StringUtil.isBlankOrDefault(transport, Constant.DEFAULT_TRANSPORTER);
        sender = ExtensionLoader.loadExtension(Http2StreamSender.class, transport);
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
