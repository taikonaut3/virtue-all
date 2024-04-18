package io.virtue.rpc.virtue;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.rpc.protocol.AbstractProtocol;
import io.virtue.rpc.virtue.envelope.VirtueRequest;
import io.virtue.rpc.virtue.envelope.VirtueResponse;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.channel.Channel;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * Virtue Protocol implementation.
 */
@Extension(VIRTUE)
public final class VirtueProtocol extends AbstractProtocol<VirtueRequest, VirtueResponse> {

    public VirtueProtocol() {
        super(VIRTUE,
                new VirtueCodec(VirtueResponse.class, VirtueRequest.class),
                new VirtueCodec(VirtueRequest.class, VirtueResponse.class),
                new VirtueProtocolParser(),
                new VirtueInvokerFactory());
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
            result = "Server invoke Exception:" + e.getMessage();
            hasException = true;
        }
        URL url = invocation.url();
        url.addParam(Key.BODY_TYPE, result.getClass().getName());
        return new VirtueResponse(url, result, hasException);
    }

    @Override
    protected VirtueResponse createResponse(URL url, Throwable e) {
        url.addParam(Key.BODY_TYPE, String.class.getName());
        return new VirtueResponse(url, e.getMessage(), true);
    }

}
