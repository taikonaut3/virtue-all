package io.github.astro.virtue.rpc.protocol;

import io.github.astro.virtue.common.extension.RpcContext;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.manager.Virtue;
import io.github.astro.virtue.transport.Transporter;
import io.github.astro.virtue.transport.channel.ChannelHandler;
import io.github.astro.virtue.transport.code.Codec;
import io.github.astro.virtue.transport.server.Server;

public abstract class AbstractProtocol<Req, Res> implements Protocol<Req, Res> {

    protected String protocol;

    protected Codec serverCodec;

    protected Codec clientCodec;

    protected ChannelHandler clientHandler;

    protected ChannelHandler serverHandler;

    protected ProtocolParser protocolParser;

    protected final Transporter transporter;



    public AbstractProtocol(String protocol, Codec serverCodec, Codec clientCodec,
                            ChannelHandler clientHandler, ChannelHandler serverHandler,
                            ProtocolParser protocolParser) {
        this.protocol = protocol;
        this.serverCodec = serverCodec;
        this.clientCodec = clientCodec;
        this.clientHandler = clientHandler;
        this.serverHandler = serverHandler;
        this.protocolParser = protocolParser;
        Virtue virtue = RpcContext.getContext().attribute(Virtue.ATTRIBUTE_KEY).get();
        String transport = virtue.configManager().applicationConfig().transport();
        transporter = ExtensionLoader.loadService(Transporter.class, transport);
    }

    @Override
    public Server openServer(URL url) {
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
