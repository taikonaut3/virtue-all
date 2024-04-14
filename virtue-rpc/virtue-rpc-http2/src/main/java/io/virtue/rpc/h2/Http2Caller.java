package io.virtue.rpc.h2;

import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteCaller;
import io.virtue.rpc.h2.config.Http2Call;
import io.virtue.rpc.h2.envelope.Http2Request;
import io.virtue.rpc.support.AbstractCaller;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.client.Client;
import io.virtue.transport.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.List;

import static io.virtue.common.constant.Components.Protocol.HTTP2;

/**
 * Http2 protocol caller.
 */
@Getter
@Accessors(fluent = true)
public class Http2Caller extends AbstractCaller<Http2Call> {

    private Http2Invoker invoker;

    public Http2Caller(Method method, RemoteCaller<?> remoteCaller) {
        super(method, remoteCaller, HTTP2, Http2Call.class);
    }

    @Override
    protected void doInit() {
        invoker = new Http2Invoker(parsedAnnotation, this);
    }

    @Override
    protected URL createUrl(URL serverUrl) {
        URL url = super.createUrl(serverUrl);
        url.addParams(invoker.parameterization());
        return url;
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        // parse dynamic url
        URL url = invocation.url();
        url.set(HttpMethod.ATTRIBUTE_KEY, invoker.httpMethod());
        Http2Request request = ((Http2Protocol) protocolInstance).createRequest(invocation);
        url.set(Http2Request.ATTRIBUTE_KEY, request);
        return super.invoke(invocation);
    }

    @Override
    public List<String> pathList() {
        return URL.pathToList(invoker.path());
    }

    @Override
    protected void send(Client client, Invocation invocation, RpcFuture future) {
        Http2Request request = invocation.url().get(Http2Request.ATTRIBUTE_KEY);
        var transportRequest = ((Http2Protocol) protocolInstance).convertToTransportRequest(request);
        invoker.sender().send(client, transportRequest, future);
    }
}
