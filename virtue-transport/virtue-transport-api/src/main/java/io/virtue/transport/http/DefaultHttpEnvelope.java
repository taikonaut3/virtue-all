package io.virtue.transport.http;

import io.virtue.common.url.URL;
import io.virtue.transport.http.h1.HttpEnvelope;
import io.virtue.transport.http.h1.HttpHeaders;

/**
 * Default HttpEnvelope.
 */
public class DefaultHttpEnvelope implements HttpEnvelope {

    protected final HttpVersion version;
    protected final HttpMethod method;
    protected final URL url;
    protected final HttpHeaders headers;
    protected final byte[] data;

    public DefaultHttpEnvelope(HttpVersion version, HttpMethod method, URL url, HttpHeaders headers, byte[] data) {
        this.version = version;
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.data = data;
        if (data != null && data.length > 0) {
            headers.add("content-length", String.valueOf(data.length));
        }
    }

    @Override
    public HttpVersion version() {
        return version;
    }

    @Override
    public HttpMethod method() {
        return method;
    }

    @Override
    public URL url() {
        return url;
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public byte[] data() {
        return data;
    }
}
