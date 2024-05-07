package io.virtue.transport.netty.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.virtue.common.url.URL;
import io.virtue.transport.http.DefaultHttpEnvelope;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h1.HttpHeaders;
import io.virtue.transport.http.h1.HttpResponse;

import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.valueOf;
import static io.virtue.transport.http.HttpMethod.getOf;
import static io.virtue.transport.netty.ProtocolAdapter.buildHttpHeaders;

/**
 * Http request base on netty.
 */
public class NettyHttpResponse extends DefaultHttpEnvelope implements HttpResponse {

    private final HttpResponseStatus responseStatus;

    public NettyHttpResponse(HttpVersion version, URL url, HttpResponseStatus responseStatus, HttpHeaders headers, byte[] data) {
        super(version, getOf(url), url, headers, data);
        this.responseStatus = responseStatus;
    }

    public NettyHttpResponse(HttpVersion version, URL url, int statusCode, Map<CharSequence, CharSequence> headers, byte[] data) {
        this(version, url, valueOf(statusCode), buildHttpHeaders(version, headers), data);
    }

    @Override
    public int statusCode() {
        return responseStatus.code();
    }
}
