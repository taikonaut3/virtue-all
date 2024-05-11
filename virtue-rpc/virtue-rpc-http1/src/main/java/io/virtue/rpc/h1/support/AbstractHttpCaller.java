package io.virtue.rpc.h1.support;

import io.virtue.common.constant.Key;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.URL;
import io.virtue.common.util.CollectionUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.core.RemoteCaller;
import io.virtue.rpc.h1.parse.JaxRsRestInvocationParser;
import io.virtue.rpc.h1.parse.RestInvocationParser;
import io.virtue.rpc.support.AbstractCaller;
import io.virtue.transport.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.virtue.rpc.h1.support.HttpUtil.regularRequestHeaders;

/**
 * Standard Abstract Http Caller.
 */
@Accessors(fluent = true)
public abstract class AbstractHttpCaller<T extends Annotation> extends AbstractCaller<T> {

    private static final Map<CharSequence, CharSequence> REGULAR_REQUEST_HEADERS = regularRequestHeaders();

    protected String path;
    @Getter
    protected HttpMethod httpMethod;
    @Parameter(Key.SSL)
    protected boolean ssl;
    @Getter
    protected Map<CharSequence, CharSequence> queryParams;
    @Getter
    protected Map<CharSequence, CharSequence> requestHeaders;
    @Getter
    protected RestInvocationParser restInvocationParser;

    protected AbstractHttpCaller(Method method, RemoteCaller<?> remoteCaller, String protocol, Class<T> annoType) {
        super(method, remoteCaller, protocol, annoType);
        addRequestHeaders(REGULAR_REQUEST_HEADERS);
        restInvocationParser = new JaxRsRestInvocationParser();
    }

    @Override
    protected URL createUrl(URL clientUrl) {
        URL url = super.createUrl(clientUrl);
        url.set(HttpMethod.ATTRIBUTE_KEY, httpMethod);
        return url;
    }

    @Override
    public List<String> pathList() {
        return URL.pathToList(path);
    }

    public void addQueryParams(Map<CharSequence, CharSequence> headers) {
        if (CollectionUtil.isNotEmpty(headers)) {
            headers.forEach(this::addRequestHeader);
        }
    }

    public void addQueryParam(CharSequence key, CharSequence value) {
        if (queryParams == null) {
            queryParams = new LinkedHashMap<>();
        }
        if (!StringUtil.isBlank(key) && !StringUtil.isBlank(value)) {
            queryParams.put(key, value);
        }
    }

    public void addRequestHeaders(Map<CharSequence, CharSequence> headers) {
        if (CollectionUtil.isNotEmpty(headers)) {
            headers.forEach(this::addRequestHeader);
        }
    }

    public void addRequestHeader(CharSequence key, CharSequence value) {
        if (requestHeaders == null) {
            requestHeaders = new LinkedHashMap<>();
        }
        if (!StringUtil.isBlank(key) && !StringUtil.isBlank(value)) {
            requestHeaders.put(key, value);
        }
    }
}
