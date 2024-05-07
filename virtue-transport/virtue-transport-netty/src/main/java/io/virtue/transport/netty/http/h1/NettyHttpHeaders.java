package io.virtue.transport.netty.http.h1;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.virtue.common.util.CollectionUtil;
import io.virtue.transport.http.h1.HttpHeaders;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

/**
 * Netty HttpHeaders.
 */
public class NettyHttpHeaders implements HttpHeaders {

    private final io.netty.handler.codec.http.HttpHeaders headers;

    public NettyHttpHeaders() {
        headers = new DefaultHttpHeaders();
    }

    public NettyHttpHeaders(Map<CharSequence, CharSequence> headers) {
        this();
        add(headers);
    }

    public NettyHttpHeaders(io.netty.handler.codec.http.HttpHeaders headers) {
        this();
        for (Map.Entry<String, String> header : headers) {
            add(header.getKey(), header.getValue());
        }
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
        if (CollectionUtil.isNotEmpty(headers)) {
            headers.forEach(this::add);
        }
    }

    @Override
    public void add(HttpHeaders headers) {
        for (Map.Entry<CharSequence, CharSequence> header : headers) {
            add(header.getKey(), header.getValue());
        }
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iterator() {
        return headers.iteratorCharSequence();
    }

    /**
     * Get http headers.
     *
     * @return
     */
    public io.netty.handler.codec.http.HttpHeaders httpHeaders() {
        return headers;
    }
}
