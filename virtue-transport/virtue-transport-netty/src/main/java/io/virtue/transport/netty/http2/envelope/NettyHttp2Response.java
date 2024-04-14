package io.virtue.transport.netty.http2.envelope;

import io.virtue.common.url.URL;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h2.Http2Response;
import io.virtue.transport.netty.http.envelope.DefaultHttpEnvelope;

import java.util.Map;

/**
 * Http2 response base on netty.
 */
public class NettyHttp2Response extends DefaultHttpEnvelope implements Http2Response {

    private final int statusCode;
    private StreamEnvelope streamEnvelope;

    public NettyHttp2Response(StreamEnvelope streamEnvelope) {
        super(HttpVersion.HTTP_2_0,
                streamEnvelope.method(),
                streamEnvelope.url(),
                streamEnvelope.headers(),
                streamEnvelope.data());
        this.streamEnvelope = streamEnvelope;
        statusCode = Integer.parseInt(((NettyHttp2Headers) streamEnvelope.headers()).headers().status().toString());
    }

    public NettyHttp2Response(int statusCode, URL url, Map<CharSequence, CharSequence> headers, byte[] body) {
        super(HttpVersion.HTTP_2_0,
                url.get(HttpMethod.ATTRIBUTE_KEY),
                url,
                new NettyHttp2Headers(headers),
                body);
        this.statusCode = statusCode;
    }

    @Override
    public int statusCode() {
        return statusCode;
    }

    /**
     * Get stream envelope.
     *
     * @return
     */
    public StreamEnvelope streamEnvelope() {
        return streamEnvelope;
    }
}
