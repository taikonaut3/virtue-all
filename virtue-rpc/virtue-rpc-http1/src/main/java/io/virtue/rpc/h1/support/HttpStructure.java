package io.virtue.rpc.h1.support;

import io.virtue.common.util.AssertUtil;
import io.virtue.transport.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Envelope of HTTP structures.
 */
@Getter
@Accessors(fluent = true)
public class HttpStructure {

    private final Map<CharSequence, CharSequence> headers = new LinkedHashMap<>();
    private final Map<CharSequence, CharSequence> params = new LinkedHashMap<>();
    private String path;
    private HttpMethod method;
    private Object body;

    public HttpStructure() {

    }

    public HttpStructure(String path, HttpMethod method,
                         Map<CharSequence, CharSequence> headers,
                         Map<CharSequence, CharSequence> params,
                         Object body) {
        allArgsConstructor(path, method, headers, params, body);
    }

    /**
     * todo when super before can insert code.
     *
     * @param path
     * @param method
     * @param headers
     * @param params
     * @param body
     */
    public void allArgsConstructor(String path, HttpMethod method,
                                   Map<CharSequence, CharSequence> headers,
                                   Map<CharSequence, CharSequence> params,
                                   Object body) {
        AssertUtil.notNull(path, method);
        this.path = path;
        this.method = method;
        if (headers != null) {
            this.headers.putAll(headers);
        }
        if (params != null) {
            this.params.putAll(params);
        }
        this.body = body;
    }

    /**
     * Set path.
     *
     * @param path
     */
    public void path(String path) {
        this.path = path;
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

    public CharSequence getParam(CharSequence name) {
        return params.get(name);
    }

    public void addParam(CharSequence name, CharSequence value) {
        params.put(name, value);
    }

    public void addParams(Map<CharSequence, CharSequence> params) {
        this.params.putAll(params);
    }
}
