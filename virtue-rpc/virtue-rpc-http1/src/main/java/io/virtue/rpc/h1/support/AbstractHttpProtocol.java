package io.virtue.rpc.h1.support;

import io.virtue.common.exception.ResourceException;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.Invocation;
import io.virtue.rpc.protocol.AbstractProtocol;
import io.virtue.serialization.Serializer;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.Transporter;
import io.virtue.transport.http.HttpTransporter;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.MediaType;
import io.virtue.transport.http.VirtueHttpHeaderNames;
import io.virtue.transport.http.h1.HttpRequest;
import io.virtue.transport.http.h1.HttpResponse;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.virtue.transport.http.HttpHeaderNames.CONTENT_TYPE;
import static io.virtue.transport.http.HttpHeaderNames.HOST;

/**
 * Abstract HttpProtocol.
 */
public abstract class AbstractHttpProtocol extends AbstractProtocol<HttpRequest, HttpResponse> {

    private final HttpVersion version;

    protected AbstractHttpProtocol(String protocol, HttpVersion version) {
        super(protocol);
        this.version = version;
    }

    @Override
    public Invocation createInvocation(Caller<?> caller, Object[] args) {
        return new HttpInvocation(caller, args);
    }

    @Override
    public Invocation createInvocation(URL url, Callee<?> callee, Object[] args) {
        return new HttpInvocation(url, callee, args);
    }

    @Override
    public HttpRequest createRequest(Invocation invocation) {
        HttpInvocation httpInvocation = (HttpInvocation) invocation;
        httpInvocation.headers().put(HOST, httpInvocation.url().address());
        httpInvocation.headers().put(VirtueHttpHeaderNames.VIRTUE_URL.getName(), httpInvocation.url().toString());
        byte[] data = HttpUtil.serialize(httpInvocation.getHeader(CONTENT_TYPE), httpInvocation.body());
        URL requestUrl = httpInvocation.url().replicate();
        requestUrl.params().clear();
        httpInvocation.params().forEach((k, v) -> requestUrl.addParam(k.toString(), v.toString()));
        return httpTransporter().newRequest(version, requestUrl, httpInvocation.headers(), data);
    }

    @Override
    public HttpResponse createResponse(Invocation invocation, Object result) {
        int statusCode = 200;
        if (result instanceof Exception e) {
            result = SERVER_INVOKE_EXCEPTION + e.getMessage();
            statusCode = 500;
        }
        HttpInvocation httpInvocation = (HttpInvocation) invocation;
        byte[] data = HttpUtil.serialize(httpInvocation.headers().get(CONTENT_TYPE), result);
        URL requestUrl = httpInvocation.url().replicate();
        requestUrl.params().clear();
        httpInvocation.params().forEach((k, v) -> requestUrl.addParam(k.toString(), v.toString()));
        return httpTransporter().newResponse(version, requestUrl, statusCode, httpInvocation.headers(), data);
    }

    @Override
    public HttpResponse createResponse(URL url, Throwable e) {
        int statusCode = 500;
        if (e instanceof HttpException httpException) {
            statusCode = httpException.statusCode();
        }
        String errorMessage = SERVER_EXCEPTION + e.getMessage();
        Map<CharSequence, CharSequence> headers = new LinkedHashMap<>();
        headers.put(CONTENT_TYPE, MediaType.APPLICATION_JSON.getName());
        headers.putAll(HttpUtil.regularResponseHeaders());
        return httpTransporter().newResponse(version, url, statusCode, headers, errorMessage.getBytes());
    }

    @Override
    public Invocation parseOfRequest(Request request) {
        try {
            return super.parseOfRequest(request);
        } catch (ResourceException e) {
            throw new HttpException(404, e.getMessage());
        }
    }

    @Override
    protected Object[] parseToInvokeArgs(Request request, HttpRequest HttpRequest, Callee<?> callee) {
        if (callee instanceof AbstractHttpCallee<?> httpCallee) {
            return httpCallee.restInvocationParser().parse(HttpRequest, callee);
        }
        throw new UnsupportedOperationException("unSupport parse");
    }

    @Override
    protected Object parseToReturnValue(Response response, HttpResponse httpResponse, Caller<?> caller) {
        byte[] data = httpResponse.data();
        if (!(caller.returnClass() == Void.class)) {
            if (data != null && data.length > 0) {
                if (httpResponse.statusCode() == 200) {
                    String contentType = httpResponse.headers().get(CONTENT_TYPE).toString();
                    Serializer serializer = HttpUtil.getSerializer(contentType);
                    return serializer.deserialize(data, caller.returnType());
                } else {
                    return new String(data);
                }
            }
        }
        return null;
    }

    @Override
    protected Transporter loadTransporter(String transport) {
        return ExtensionLoader.loadExtension(HttpTransporter.class, transport);
    }

    /**
     * Get HttpTransporter.
     *
     * @return
     */
    public HttpTransporter httpTransporter() {
        return (HttpTransporter) transporter;
    }
}
