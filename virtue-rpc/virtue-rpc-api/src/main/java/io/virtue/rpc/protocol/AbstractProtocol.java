package io.virtue.rpc.protocol;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.Virtue;
import io.virtue.rpc.handler.BaseClientChannelHandlerChain;
import io.virtue.rpc.handler.BaseServerChannelHandlerChain;
import io.virtue.transport.Transporter;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.server.Server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract Protocol.
 *
 * @param <Req> request type
 * @param <Resp> response type
 */
public abstract class AbstractProtocol<Req, Resp> implements Protocol<Req, Resp> {

    private Virtue virtue;

    protected String protocol;

    protected Codec serverCodec;

    protected Codec clientCodec;

    private final Map<String, Client> multiplexClients = new ConcurrentHashMap<>();

    private final Map<String, Client> customClients = new ConcurrentHashMap<>();

    protected ChannelHandler clientHandler;

    protected ChannelHandler serverHandler;

    protected ProtocolParser protocolParser;

    protected Transporter transporter;

    protected String transport;

    protected AbstractProtocol(String protocol, Codec serverCodec, Codec clientCodec, ProtocolParser protocolParser) {
        this(protocol, serverCodec, clientCodec, new BaseClientChannelHandlerChain(), new BaseServerChannelHandlerChain(), protocolParser);
    }

    protected AbstractProtocol(String protocol, Codec serverCodec, Codec clientCodec,
                               ChannelHandler clientHandler, ChannelHandler serverHandler,
                               ProtocolParser protocolParser) {
        this.protocol = protocol;
        this.serverCodec = serverCodec;
        this.clientCodec = clientCodec;
        this.clientHandler = clientHandler;
        this.serverHandler = serverHandler;
        this.protocolParser = protocolParser;
    }

    /**
     * Set virtue by {@link io.virtue.common.spi.LoadedListener}.
     *
     * @param virtue
     */
    public void virtue(Virtue virtue) {
        this.virtue = virtue;
        String transportName = virtue.configManager().applicationConfig().transport();
        transporter = loadTransporter(transportName);
    }

    @Override
    public Client openClient(URL url) {
        boolean isMultiplex = url.getBooleanParam(Key.MULTIPLEX, false);
        Client client;
        if (isMultiplex) {
            String key = url.authority();
            client = getClient(url, key, multiplexClients);
        } else {
            String key = url.uri();
            client = getClient(url, key, customClients);
        }
        return client;
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

    protected Transporter loadTransporter(String transport) {
        return ExtensionLoader.loadExtension(Transporter.class, transport);
    }

    private Client getClient(URL url, String key, Map<String, Client> clients) {
        Client client = clients.computeIfAbsent(key, k -> transporter.connect(url, clientHandler, clientCodec));
        if (!client.isActive()) {
            client.connect();
        }
        return client;
    }
}
