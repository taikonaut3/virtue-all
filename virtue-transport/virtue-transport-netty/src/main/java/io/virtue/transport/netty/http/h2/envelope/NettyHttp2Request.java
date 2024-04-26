package io.virtue.transport.netty.http.h2.envelope;

import io.virtue.common.url.URL;
import io.virtue.transport.http.DefaultHttpEnvelope;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h2.Http2Request;

import java.util.Map;

/**
 * Http2 request base on netty.
 */
public class NettyHttp2Request extends DefaultHttpEnvelope implements Http2Request {


    public NettyHttp2Request(URL url, Map<CharSequence, CharSequence> headers, byte[] body) {
        super(HttpVersion.HTTP_2_0, HttpMethod.getOf(url), url, new NettyHttp2Headers(headers), body);
    }

    public NettyHttp2Request(StreamEnvelope streamEnvelope) {
        super(HttpVersion.HTTP_2_0,
                streamEnvelope.method(),
                streamEnvelope.url(),
                streamEnvelope.headers(),
                streamEnvelope.data());
    }

}
