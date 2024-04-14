package io.virtue.rpc.h2;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteService;
import io.virtue.rpc.h2.config.Http2Callable;
import io.virtue.rpc.h2.envelope.Http2Response;
import io.virtue.rpc.support.AbstractCallee;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.List;

import static io.virtue.common.constant.Components.Protocol.HTTP2;

/**
 * Http2 protocol callee.
 */
@Getter
@Accessors(fluent = true)
public class Http2Callee extends AbstractCallee<Http2Callable> {

    private Http2Invoker invoker;

    public Http2Callee(Method method, RemoteService<?> remoteService) {
        super(method, remoteService, HTTP2, Http2Callable.class);
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
        URL url = invocation.url();
        url.set(HttpMethod.ATTRIBUTE_KEY, invoker.httpMethod());
        Object result = null;
        try {
            result = doInvoke(invocation);
            sendSuccessMessageEvent(invocation, result);
        } catch (RpcException e) {
            sendErrorMessageEvent(invocation, e);
        }
        return result;
    }

    @Override
    protected void sendSuccess(Invocation invocation, Channel channel, Object result) throws RpcException {
        URL url = invocation.url();
        url.addParams(this.url.params());
        Http2Response response = (Http2Response) url.get(Key.SERVICE_RESPONSE);
        response.addHeaders(invoker.headers());
        response.statusCode(200);
        var transportResponse = ((Http2Protocol) protocolInstance).convertToTransportResponse(response);
        invoker.sender().send(channel, transportResponse);
    }

    @Override
    protected void sendError(Invocation invocation, Channel channel, Throwable cause) throws RpcException {

    }

    @Override
    public List<String> pathList() {
        return URL.pathToList(invoker.path());
    }
}
