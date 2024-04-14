package io.virtue.transport.http;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.AttributeKey;

/**
 * Http Method.
 */
public enum HttpMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    public static final AttributeKey<HttpMethod> ATTRIBUTE_KEY = AttributeKey.of(Key.HTTP_METHOD);

}
