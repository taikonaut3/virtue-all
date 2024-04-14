package io.virtue.rpc.h2.envelope;

import io.virtue.common.extension.AttributeKey;
import io.virtue.core.Invocation;
import io.virtue.rpc.h2.Http2Caller;
import io.virtue.rpc.h2.HttpUtil;
import lombok.Getter;
import lombok.experimental.Accessors;

import static io.virtue.transport.util.TransportUtil.getHttpMethod;

/**
 * The top-level Http 2 Request, which can be changed by the user extension.
 */
@Getter
@Accessors(fluent = true)
public class Http2Request extends Http2Envelope {

    public static final AttributeKey<Http2Request> ATTRIBUTE_KEY = AttributeKey.of("http2Request");

    public Http2Request(Invocation invocation) {
        super(invocation.url(), getHttpMethod(invocation));
        addHeaders(((Http2Caller) invocation.invoker()).invoker().headers());
        body = HttpUtil.findBody(invocation);
    }

    public Http2Request(io.virtue.transport.http.h2.Http2Request http2Request) {
        super(http2Request.url(), http2Request.method(), http2Request.headers(), http2Request.data());

    }
}
