package io.virtue.rpc.protocol;

import io.virtue.common.extension.RpcContext;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.manager.Virtue;
import io.virtue.transport.Transporter;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.server.Server;

public abstract class AbstractProtocol<Req, Res> implements Protocol<Req, Res> {

    protected String protocol;

    protected Codec serverCodec;

    protected Codec clientCodec;

    protected ChannelHandler clientHandler;

    protected ChannelHandler serverHandler;

    protected ProtocolParser protocolParser;

    protected Transporter transporter;

    public AbstractProtocol(String protocol, Codec serverCodec, Codec clientCodec,
                            ChannelHandler clientHandler, ChannelHandler serverHandler,
                            ProtocolParser protocolParser) {
        this.protocol = protocol;
        this.serverCodec = serverCodec;
        this.clientCodec = clientCodec;
        this.clientHandler = clientHandler;
        this.serverHandler = serverHandler;
        this.protocolParser = protocolParser;
        Virtue virtue = RpcContext.currentContext().attribute(Virtue.ATTRIBUTE_KEY).get();
        transporter = virtue.attribute(Transporter.ATTRIBUTE_KEY).get();
        if (transporter == null) {
            String transport = virtue.configManager().applicationConfig().transport();
            transporter = ExtensionLoader.loadService(Transporter.class, transport);
        }
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
