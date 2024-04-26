package io.virtue.transport.http;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;

/**
 * Http Method.
 */
public enum HttpMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    public static final AttributeKey<HttpMethod> ATTRIBUTE_KEY = AttributeKey.of(Key.HTTP_METHOD);

    public static HttpMethod getOf(Invocation invocation) {
        return getOf(invocation.url());
    }

    public static HttpMethod getOf(URL url) {
        return url.get(HttpMethod.ATTRIBUTE_KEY);
    }

}
