package io.github.astro.virtue.rpc.protocol;

import io.github.astro.virtue.common.constant.Constant;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.Transporter;
import io.github.astro.virtue.transport.channel.ChannelHandler;
import io.github.astro.virtue.transport.client.Client;
import io.github.astro.virtue.transport.code.Codec;
import io.github.astro.virtue.transport.server.Server;

public abstract class AbstractProtocol<Req, Res> implements Protocol<Req, Res> {

    protected String protocol;

    protected volatile Codec serverCodec;

    protected volatile Codec clientCodec;

    protected ChannelHandler clientHandler;

    protected ChannelHandler serverHandler;

    protected volatile ProtocolParser protocolParser;

    public AbstractProtocol(String protocol, Codec serverCodec, Codec clientCodec,
                            ChannelHandler clientHandler, ChannelHandler serverHandler,
                            ProtocolParser protocolParser) {
        this.protocol = protocol;
        this.serverCodec = serverCodec;
        this.clientCodec = clientCodec;
        this.clientHandler = clientHandler;
        this.serverHandler = serverHandler;
        this.protocolParser = protocolParser;
    }

    @Override
    public Client openClient(URL url) {
        String transporterKey = url.getParameter(Key.TRANSPORTER, Constant.DEFAULT_TRANSPORTER);
        Transporter transporter = ExtensionLoader.loadService(Transporter.class, transporterKey);
        return transporter.connect(url, clientHandler, clientCodec);
    }

    @Override
    public Server openServer(URL url) {
        String transporterKey = url.getParameter(Key.TRANSPORTER, Constant.DEFAULT_TRANSPORTER);
        Transporter transporter = ExtensionLoader.loadService(Transporter.class, transporterKey);
        return transporter.bind(url, serverHandler, serverCodec);
    }

    @Override
    public Codec serverCodec() {
        return serverCodec;
    }

    @Override
    public Codec clientCodec() {
        return clientCodec;
    }

    @Override
    public ProtocolParser parser() {
        return protocolParser;
    }

    @Override
    public String protocol() {
        return protocol;
    }
}
