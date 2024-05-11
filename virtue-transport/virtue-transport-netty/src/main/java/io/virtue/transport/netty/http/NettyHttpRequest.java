package io.virtue.transport.netty.http;

import io.virtue.common.url.URL;
import io.virtue.transport.http.DefaultHttpEnvelope;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h1.HttpHeaders;
import io.virtue.transport.http.h1.HttpRequest;

import java.util.Map;

import static io.virtue.transport.http.HttpMethod.getOf;
import static io.virtue.transport.netty.ProtocolAdapter.buildHttpHeaders;

/**
 * Http response base on netty.
 */
public class NettyHttpRequest extends DefaultHttpEnvelope implements HttpRequest {

    public NettyHttpRequest(HttpVersion version, URL url, HttpHeaders headers, byte[] data) {
        super(version, getOf(url), url, headers, data);
    }

    public NettyHttpRequest(HttpVersion version, URL url, Map<CharSequence, CharSequence> headers, byte[] data) {
        this(version, url, buildHttpHeaders(version, headers), data);
    }
}
