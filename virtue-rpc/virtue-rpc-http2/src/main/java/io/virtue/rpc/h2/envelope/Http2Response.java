package io.virtue.rpc.h2.envelope;

import io.virtue.common.extension.AttributeKey;
import io.virtue.common.url.URL;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static io.virtue.transport.util.TransportUtil.getHttpMethod;

/**
 * The top-level Http 2 Response, which can be changed by the user extension.
 */
@Getter
@Setter
@Accessors(fluent = true)
public class Http2Response extends Http2Envelope {

    public static final AttributeKey<Http2Response> ATTRIBUTE_KEY = AttributeKey.of("http2Response");

    public static final AttributeKey<Integer> STATUS_CODE = AttributeKey.of("statusCode");

    private int statusCode;

    public Http2Response(URL url, Object payload) {
        super(url, getHttpMethod(url));
        body = payload;
    }

    public Http2Response(io.virtue.transport.http.h2.Http2Response http2Response) {
        super(http2Response.url(), http2Response.method(), http2Response.headers(), http2Response.data());
        this.statusCode = http2Response.statusCode();
    }
}
