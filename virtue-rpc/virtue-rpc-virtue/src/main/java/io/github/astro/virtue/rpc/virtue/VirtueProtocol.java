package io.github.astro.virtue.rpc.virtue;

import io.github.astro.virtue.common.constant.*;
import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.rpc.protocol.AbstractProtocol;
import io.github.astro.virtue.rpc.virtue.client.VirtueClientChannelHandlerChain;
import io.github.astro.virtue.rpc.virtue.envelope.VirtueRequest;
import io.github.astro.virtue.rpc.virtue.envelope.VirtueResponse;
import io.github.astro.virtue.rpc.virtue.header.Header;
import io.github.astro.virtue.rpc.virtue.header.VirtueHeader;
import io.github.astro.virtue.rpc.virtue.server.VirtueServerChannelHandlerChain;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

@ServiceProvider(VIRTUE)
public final class VirtueProtocol extends AbstractProtocol<VirtueRequest, VirtueResponse> {

    public VirtueProtocol() {
        super(VIRTUE,
                new VirtueCodec(VirtueResponse.class, VirtueRequest.class),
                new VirtueCodec(VirtueRequest.class, VirtueResponse.class),
                new VirtueClientChannelHandlerChain(),
                new VirtueServerChannelHandlerChain(),
                new VirtueProtocolParser());
    }

    @Override
    public VirtueRequest createRequest(URL url, Object payload) {
        url.addParameter(Key.ENVELOPE, Components.Envelope.REQUEST);
        return new VirtueRequest(createHeader(url, Components.Envelope.REQUEST), payload);
    }

    @Override
    public VirtueResponse createResponse(URL url, Object payload) {
        url.addParameter(Key.ENVELOPE, Components.Envelope.RESPONSE);
        return new VirtueResponse(createHeader(url, Components.Envelope.RESPONSE), payload);
    }

    private Header createHeader(URL url, String envelope) {
        String serialize = url.getParameter(Key.SERIALIZE, Constant.DEFAULT_SERIALIZE);
        Mode serializeMode = ModeContainer.getMode(Key.SERIALIZE, serialize);
        Mode responseMode = ModeContainer.getMode(Key.ENVELOPE, envelope);
        Mode protocolMode = ModeContainer.getMode(Key.PROTOCOL, protocol());
        VirtueHeader header = new VirtueHeader(serializeMode, responseMode, protocolMode);
        header.addExtendData(Key.URL, url.toString());
        return header;
    }

}
