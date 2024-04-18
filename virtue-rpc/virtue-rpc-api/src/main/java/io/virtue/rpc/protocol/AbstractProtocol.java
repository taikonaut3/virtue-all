package io.virtue.rpc.protocol;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.common.util.DateUtil;
import io.virtue.core.Caller;
import io.virtue.core.Invocation;
import io.virtue.core.InvokerFactory;
import io.virtue.core.Virtue;
import io.virtue.core.config.ClientConfig;
import io.virtue.core.manager.ClientConfigManager;
import io.virtue.rpc.event.SendMessageEvent;
import io.virtue.rpc.handler.BaseClientChannelHandlerChain;
import io.virtue.rpc.handler.BaseServerChannelHandlerChain;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.Transporter;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.server.Server;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract Protocol.
 *
 * @param <Req>  request type
 * @param <Resp> response type
 */
@Accessors(fluent = true)
public abstract class AbstractProtocol<Req, Resp> implements Protocol {

    private final Map<String, Client> multiplexClients = new ConcurrentHashMap<>();
    private final Map<String, Client> customClients = new ConcurrentHashMap<>();
    protected String protocol;
    protected Codec serverCodec;
    protected Codec clientCodec;
    @Getter
    protected ChannelHandler clientHandler;
    @Getter
    protected ChannelHandler serverHandler;
    protected ProtocolParser protocolParser;
    protected InvokerFactory invokerFactory;
    protected Transporter transporter;
    protected String transport;
    protected Virtue virtue;

    protected AbstractProtocol(String protocol, ProtocolParser protocolParser, InvokerFactory invokerFactory) {
        this(protocol, null, null, protocolParser, invokerFactory);
    }

    protected AbstractProtocol(String protocol, Codec serverCodec, Codec clientCodec,
                               ProtocolParser protocolParser, InvokerFactory invokerFactory) {
        this(protocol, serverCodec, clientCodec,
                new BaseClientChannelHandlerChain(),
                new BaseServerChannelHandlerChain(),
                protocolParser,
                invokerFactory);
    }

    protected AbstractProtocol(String protocol, Codec serverCodec, Codec clientCodec,
                               ChannelHandler clientHandler, ChannelHandler serverHandler,
                               ProtocolParser protocolParser, InvokerFactory invokerFactory) {
        this.protocol = protocol;
        this.serverCodec = serverCodec;
        this.clientCodec = clientCodec;
        this.clientHandler = clientHandler;
        this.serverHandler = serverHandler;
        this.protocolParser = protocolParser;
        if (protocolParser instanceof AbstractProtocolParser abstractProtocolParser) {
            abstractProtocolParser.protocol(this);
        }
        this.invokerFactory = invokerFactory;
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
    public RpcFuture sendRequest(Invocation invocation) {
        RpcFuture future = new RpcFuture(invocation);
        // Leave it to the io thread to send the request.
        SendMessageEvent event = new SendMessageEvent(() -> {
            Req request = createRequest(invocation);
            String timestamp = DateUtil.format(LocalDateTime.now(), DateUtil.COMPACT_FORMAT);
            invocation.url().addParam(Key.TIMESTAMP, timestamp);
            Client client = getClient(invocation);
            future.client(client);
            doSendRequest(future, request);
        });
        virtue.eventDispatcher().dispatch(event);
        return future;
    }

    @Override
    public void sendResponse(Channel channel, Invocation invocation, Object result) {
        SendMessageEvent event = new SendMessageEvent(() -> {
            Resp response = createResponse(invocation, result);
            doSendResponse(channel, response);
        });
        virtue.eventDispatcher().dispatch(event);
    }

    @Override
    public void sendResponse(Channel channel, URL url, Throwable cause) {
        SendMessageEvent event = new SendMessageEvent(() -> {
            Resp response = createResponse(url, cause);
            doSendResponse(channel, response);
        });
        virtue.eventDispatcher().dispatch(event);
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
    public InvokerFactory invokerFactory() {
        return invokerFactory;
    }

    @Override
    public String protocol() {
        return protocol;
    }

    protected Transporter loadTransporter(String transport) {
        return ExtensionLoader.loadExtension(Transporter.class, transport);
    }

    protected abstract void doSendRequest(RpcFuture future, Req request);

    protected abstract void doSendResponse(Channel channel, Resp response);

    protected abstract Req createRequest(Invocation invocation);

    protected abstract Resp createResponse(Invocation invocation, Object result);

    protected abstract Resp createResponse(URL url, Throwable cause);

    private Client getClient(Invocation invocation) {
        URL url = invocation.url();
        Caller<?> caller = (Caller<?>) invocation.invoker();
        ClientConfigManager clientConfigManager = virtue.configManager().clientConfigManager();
        ClientConfig clientConfig = clientConfigManager.get(caller.clientConfig());
        if (clientConfig == null) {
            clientConfig = clientConfigManager.get(url.protocol());
        }
        URL clientUrl = new URL(url.protocol(), url.address());
        clientUrl.addParams(clientConfig.parameterization());
        clientUrl.set(Virtue.CLIENT_VIRTUE, virtue);
        clientUrl.addParam(Key.CLIENT_VIRTUE, virtue.name());
        clientUrl.addParam(Key.MULTIPLEX, String.valueOf(caller.multiplex()));
        return openClient(clientUrl);
    }

    private Client getClient(URL url, String key, Map<String, Client> clients) {
        Client client = clients.computeIfAbsent(key, k -> transporter.connect(url, clientHandler, clientCodec));
        if (!client.isActive()) {
            client.connect();
        }
        return client;
    }
}
