package io.virtue.transport.netty.http2.envelope;

import io.virtue.common.url.URL;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h2.Http2Request;
import io.virtue.transport.netty.http.envelope.DefaultHttpEnvelope;

import java.util.Map;

/**
 * Http2 request base on netty.
 */
public class NettyHttp2Request extends DefaultHttpEnvelope implements Http2Request {

    private StreamEnvelope streamEnvelope;

    public NettyHttp2Request(URL url, Map<CharSequence, CharSequence> headers, byte[] body) {
        super(HttpVersion.HTTP_2_0,
                url.get(HttpMethod.ATTRIBUTE_KEY),
                url,
                new NettyHttp2Headers(headers),
                body);
    }

    public NettyHttp2Request(StreamEnvelope streamEnvelope) {
        super(HttpVersion.HTTP_2_0,
                streamEnvelope.method(),
                streamEnvelope.url(),
                streamEnvelope.headers(),
                streamEnvelope.data());
        this.streamEnvelope = streamEnvelope;
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
