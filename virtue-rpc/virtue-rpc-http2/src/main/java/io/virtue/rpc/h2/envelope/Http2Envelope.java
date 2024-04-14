package io.virtue.rpc.h2.envelope;

import io.virtue.common.url.URL;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.http.h1.HttpHeaders;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * Http2 common envelope.
 */
@Getter
@Accessors(fluent = true)
public class Http2Envelope {

    protected final URL url;

    protected final HttpMethod method;

    protected final Map<CharSequence, CharSequence> headers;

    protected Object body;

    public Http2Envelope(URL url, HttpMethod method) {
        this(url, method, new HashMap<>(), null);
    }

    public Http2Envelope(URL url, HttpMethod method, HttpHeaders headers, Object body) {
        this(url, method, new HashMap<>(), body);
        for (Map.Entry<CharSequence, CharSequence> entry : headers) {
            this.headers.put(entry.getKey(), entry.getValue());
        }
    }

    public Http2Envelope(URL url, HttpMethod method, Map<CharSequence, CharSequence> headers, Object body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    /**
     * Set body.
     *
     * @param body
     */
    public void body(Object body) {
        this.body = body;
    }

    public CharSequence getHeader(CharSequence name) {
        return headers.get(name);
    }

    public void addHeader(CharSequence name, CharSequence value) {
        headers.put(name, value);
    }

    public void addHeaders(Map<CharSequence, CharSequence> headers) {
        this.headers.putAll(headers);
    }
}
