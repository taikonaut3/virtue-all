package io.virtue.transport.netty.http2.envelope;

import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2Headers;
import io.virtue.transport.http.h1.HttpHeaders;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

/**
 * Http2 headers base on netty.
 */
public class NettyHttp2Headers implements HttpHeaders {

    private final Http2Headers headers;

    public NettyHttp2Headers() {
        headers = new DefaultHttp2Headers();
    }

    public NettyHttp2Headers(Map<CharSequence, CharSequence> headers) {
        this();
        add(headers);
    }

    public NettyHttp2Headers(Http2Headers headers) {
        this.headers = headers;
    }

    @Override
    public CharSequence get(CharSequence name) {
        return headers.get(name);
    }

    @Override
    public void add(CharSequence name, CharSequence value) {
        headers.add(name, value);
    }

    @Override
    public void add(Map<CharSequence, CharSequence> headers) {
        if (headers != null) {
            headers.forEach(this::add);
        }
    }

    @Override
    public void add(HttpHeaders headers) {
        if (headers instanceof NettyHttp2Headers nettyHttp2Headers) {
            add(nettyHttp2Headers.headers());
        }
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iterator() {
        return headers.iterator();
    }

    public void add(Http2Headers headers) {
        this.headers.add(headers);
    }

    /**
     * Get the netty http2 headers.
     *
     * @return
     */
    public Http2Headers headers() {
        return headers;
    }
}
