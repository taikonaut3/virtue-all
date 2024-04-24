package io.virtue.transport.netty.http2.envelope;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.virtue.common.url.URL;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h2.Http2Response;
import io.virtue.transport.netty.http.envelope.DefaultHttpEnvelope;

import java.util.Map;

import static io.virtue.transport.util.TransportUtil.getHttpMethod;

/**
 * Http2 response base on netty.
 */
public class NettyHttp2Response extends DefaultHttpEnvelope implements Http2Response {

    private final HttpResponseStatus responseStatus;

    public NettyHttp2Response(int statusCode, URL url, Map<CharSequence, CharSequence> headers, byte[] body) {
        super(HttpVersion.HTTP_2_0, getHttpMethod(url), url, new NettyHttp2Headers(headers), body);
        this.responseStatus = HttpResponseStatus.valueOf(statusCode);
    }

    public NettyHttp2Response(StreamEnvelope streamEnvelope) {
        super(HttpVersion.HTTP_2_0,
                streamEnvelope.method(),
                streamEnvelope.url(),
                streamEnvelope.headers(),
                streamEnvelope.data());
        responseStatus = HttpResponseStatus.parseLine(((NettyHttp2Headers) streamEnvelope.headers()).headers().status());
    }

    @Override
    public int statusCode() {
        return responseStatus.code();
    }

}
