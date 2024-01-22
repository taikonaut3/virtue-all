package io.github.astro.rpc.virtue;

import io.github.astro.rpc.protocol.AbstractProtocol;
import io.github.astro.rpc.protocol.ProtocolParser;
import io.github.astro.rpc.virtue.client.VirtueClientChannelHandlerChain;
import io.github.astro.rpc.virtue.envelope.VirtueRequest;
import io.github.astro.rpc.virtue.envelope.VirtueResponse;
import io.github.astro.rpc.virtue.header.Header;
import io.github.astro.rpc.virtue.header.VirtueHeader;
import io.github.astro.rpc.virtue.server.VirtueServerChannelHandlerChain;
import io.github.astro.virtue.common.constant.*;
import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.code.Codec;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

@ServiceProvider(VIRTUE)
public final class VirtueProtocol extends AbstractProtocol {

    public VirtueProtocol() {
        super(VIRTUE, new VirtueClientChannelHandlerChain(), new VirtueServerChannelHandlerChain());
    }

    @Override
    protected Codec createServerCodec(URL url) {
        return new VirtueCodec(VirtueResponse.class, VirtueRequest.class);
    }

    @Override
    protected Codec createClientCodec(URL url) {
        return new VirtueCodec(VirtueRequest.class, VirtueResponse.class);
    }

    @Override
    public Object createRequest(URL url, Object payload) {
        url.addParameter(Key.ENVELOPE, Components.Envelope.REQUEST);
        return new VirtueRequest(createHeader(url, Components.Envelope.REQUEST), payload);
    }

    @Override
    public Object createResponse(URL url, Object payload) {
        url.addParameter(Key.ENVELOPE, Components.Envelope.RESPONSE);
        return new VirtueResponse(createHeader(url, Components.Envelope.RESPONSE), payload);
    }

    @Override
    protected ProtocolParser createProtocolParser(URL url) {
        return new VirtueProtocolParser();
    }

    private Header createHeader(URL url, String envelope) {
        String serialize = url.getParameter(Key.SERIALIZE, Constant.DEFAULT_SERIALIZE);
        Mode serializeMode = ModeContainer.getMode(Key.SERIALIZE, serialize);
        Mode responseMode = ModeContainer.getMode(Key.ENVELOPE, envelope);
        Mode protocolMode = ModeContainer.getMode(Key.PROTOCOL, protocol);
        VirtueHeader header = new VirtueHeader(serializeMode, responseMode, protocolMode);
        header.addExtendData(Key.URL, url.toString());
        return header;
    }

}
