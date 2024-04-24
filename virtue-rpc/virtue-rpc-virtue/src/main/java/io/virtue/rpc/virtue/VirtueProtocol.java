package io.virtue.rpc.virtue;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.Extension;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.*;
import io.virtue.rpc.protocol.AbstractProtocol;
import io.virtue.rpc.virtue.envelope.VirtueRequest;
import io.virtue.rpc.virtue.envelope.VirtueResponse;
import io.virtue.serialization.Serializer;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.channel.Channel;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * Virtue Protocol implementation.
 */
@Extension(VIRTUE)
public final class VirtueProtocol extends AbstractProtocol<VirtueRequest, VirtueResponse> {

    public VirtueProtocol() {
        super(VIRTUE,
                new VirtueCodec(VirtueResponse.class, VirtueRequest.class),
                new VirtueCodec(VirtueRequest.class, VirtueResponse.class));
    }

    @Override
    public Callee<?> createCallee(Method method, RemoteService<?> remoteService) {
        return new VirtueCallee(method, remoteService);
    }

    @Override
    public Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new VirtueCaller(method, remoteCaller);
    }

    @Override
    public Invocation createInvocation(Caller<?> caller, Object[] args) {
        return new VirtueInvocation(caller, args);
    }

    @Override
    public Invocation createInvocation(URL url, Callee<?> callee, Object[] args) {
        return new VirtueInvocation(url, callee, args);
    }

    @Override
    protected Object[] parseToInvokeArgs(Request request, VirtueRequest virtueRequest, Callee<?> callee) {
        URL url = request.url();
        VirtueInvocation invocation = (VirtueInvocation) virtueRequest.body();
        String serializationName = url.getParam(Key.SERIALIZATION);
        Serializer serializer = ExtensionLoader.loadExtension(Serializer.class, serializationName);
        return serializer.convert(invocation.args(), callee.method().getGenericParameterTypes());
    }

    @Override
    protected Object parseToReturnValue(Response response, VirtueResponse virtueResponse, Caller<?> caller) {
        URL url = response.url();
        String serializationName = url.getParam(Key.SERIALIZATION);
        Serializer serializer = ExtensionLoader.loadExtension(Serializer.class, serializationName);
        Type returnType = caller.returnType();
        Object body = virtueResponse.body();
        body = serializer.convert(body, returnType);
        virtueResponse.body(body);
        return body;
    }

    @Override
    protected void doSendRequest(RpcFuture future, VirtueRequest virtueRequest) {
        Request request = new Request(future.invocation().url(), virtueRequest);
        future.client().send(request);
    }

    @Override
    protected void doSendResponse(Channel channel, VirtueResponse virtueResponse) {
        Response response = virtueResponse.hasException()
                ? Response.error(virtueResponse.url(), virtueResponse)
                : Response.success(virtueResponse.url(), virtueResponse);
        channel.send(response);
    }

    @Override
    protected VirtueRequest createRequest(Invocation invocation) {
        URL url = invocation.url();
        url.addParam(Key.BODY_TYPE, invocation.getClass().getName());
        return new VirtueRequest(url, invocation);
    }

    @Override
    protected VirtueResponse createResponse(Invocation invocation, Object result) {
        boolean hasException = false;
        if (result instanceof Exception e) {
            result = SERVER_INVOKE_EXCEPTION + e.getMessage();
            hasException = true;
        }
        URL url = invocation.url();
        url.addParam(Key.BODY_TYPE, result.getClass().getName());
        return new VirtueResponse(url, result, hasException);
    }

    @Override
    protected VirtueResponse createResponse(URL url, Throwable e) {
        url.addParam(Key.BODY_TYPE, String.class.getName());
        return new VirtueResponse(url, SERVER_EXCEPTION + e.getMessage(), true);
    }

}
