package io.virtue.rpc.virtue;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.rpc.protocol.AbstractProtocol;
import io.virtue.rpc.virtue.envelope.VirtueRequest;
import io.virtue.rpc.virtue.envelope.VirtueResponse;

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
    public VirtueRequest createRequest(Invocation invocation) {
        URL url = invocation.url();
        url.addParam(Key.BODY_TYPE, invocation.getClass().getName());
        return new VirtueRequest(url, invocation);
    }

    @Override
    public VirtueResponse createResponse(Invocation invocation, Object payload) {
        URL url = invocation.url();
        url.addParam(Key.BODY_TYPE, payload.getClass().getName());
        return new VirtueResponse(url, payload);
    }

    @Override
    public VirtueResponse createResponse(URL url, Throwable e) {
        url.addParam(Key.BODY_TYPE, String.class.getName());
        return new VirtueResponse(url, e.getMessage());
    }
}
