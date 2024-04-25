package io.virtue.rpc.h1;

import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.core.RemoteService;
import io.virtue.rpc.h1.parse.JaxRsRestInvocationParser;
import io.virtue.rpc.h1.parse.RestInvocationParser;
import io.virtue.rpc.support.AbstractCallee;
import io.virtue.transport.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.virtue.rpc.h1.HttpUtil.regularResponseHeaders;

/**
 * Standard Abstract Http Callee.
 */
@Accessors(fluent = true)
public abstract class AbstractHttpCallee<T extends Annotation> extends AbstractCallee<T> {

    private static final Map<CharSequence, CharSequence> REGULAR_RESPONSE_HEADERS = regularResponseHeaders();

    protected String path;

    @Getter
    protected HttpMethod httpMethod;

    @Getter
    protected Map<CharSequence, CharSequence> responseHeaders;

    @Getter
    protected RestInvocationParser restInvocationParser;

    protected AbstractHttpCallee(Method method, RemoteService<?> remoteService, String protocol, Class<T> annoType) {
        super(method, remoteService, protocol, annoType);
        addResponseHeaders(REGULAR_RESPONSE_HEADERS);
        restInvocationParser = new JaxRsRestInvocationParser();
    }

    @Override
    protected URL createUrl(URL serverUrl) {
        URL url = super.createUrl(serverUrl);
        url.set(HttpMethod.ATTRIBUTE_KEY, httpMethod);
        return url;
    }

    @Override
    public List<String> pathList() {
        return URL.pathToList(path);
    }

    protected void addResponseHeaders(Map<CharSequence, CharSequence> headers) {
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(this::addResponseHeader);
        }
    }

    protected void addResponseHeader(CharSequence key, CharSequence value) {
        if (responseHeaders == null) {
            responseHeaders = new LinkedHashMap<>();
        }
        if (!StringUtil.isBlank(key) && !StringUtil.isBlank(value)) {
            responseHeaders.put(key, value);
        }
    }

}
