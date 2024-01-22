package io.github.astro.rpc.protocol;

import io.github.astro.virtue.common.constant.Constant;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.Transporter;
import io.github.astro.virtue.transport.channel.ChannelHandler;
import io.github.astro.virtue.transport.client.Client;
import io.github.astro.virtue.transport.code.Codec;
import io.github.astro.virtue.transport.server.Server;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProtocol implements Protocol {

    private static final Logger logger = LoggerFactory.getLogger(AbstractProtocol.class);

    @Getter
    @Accessors(fluent = true)
    protected String protocol;

    protected volatile Codec serverCodec;

    protected volatile Codec clientCodec;

    protected ChannelHandler clientHandler;

    protected ChannelHandler serverHandler;

    protected volatile ProtocolParser protocolParser;

    public AbstractProtocol(String protocol, ChannelHandler clientHandler, ChannelHandler serverHandler) {
        this.protocol = protocol;
        this.clientHandler = clientHandler;
        this.serverHandler = serverHandler;
    }

    @Override
    public Client openClient(URL url) {
        String transporterKey = url.getParameter(Key.TRANSPORTER, Constant.DEFAULT_TRANSPORTER);
        Transporter transporter = ExtensionLoader.loadService(Transporter.class, transporterKey);
        return transporter.connect(url, clientHandler, getClientCodec(url));
    }

    @Override
    public Server openServer(URL url) {
        String transporterKey = url.getParameter(Key.TRANSPORTER, Constant.DEFAULT_TRANSPORTER);
        Transporter transporter = ExtensionLoader.loadService(Transporter.class, transporterKey);
        return transporter.bind(url, serverHandler, getServerCodec(url));
    }

    @Override
    public Codec getServerCodec(URL url) {
        if (serverCodec == null) {
            synchronized (this) {
                if (serverCodec == null) {
                    serverCodec = createServerCodec(url);
                    logger.debug("Created ServerCodec: {}", serverCodec);
                }
            }
        }
        return serverCodec;
    }

    @Override
    public Codec getClientCodec(URL url) {
        if (clientCodec == null) {
            synchronized (this) {
                if (clientCodec == null) {
                    clientCodec = createClientCodec(url);
                    logger.debug("Created ClientCodec: {}", clientCodec);
                }
            }
        }
        return clientCodec;
    }

    @Override
    public ProtocolParser getParser(URL url) {
        if (protocolParser == null) {
            synchronized (this) {
                if (protocolParser == null) {
                    protocolParser = createProtocolParser(url);
                    logger.debug("Created protocolParser: {}", protocolParser);
                }
            }
        }
        return protocolParser;
    }

    protected abstract Codec createServerCodec(URL url);

    protected abstract Codec createClientCodec(URL url);

    protected abstract ProtocolParser createProtocolParser(URL url);

}
