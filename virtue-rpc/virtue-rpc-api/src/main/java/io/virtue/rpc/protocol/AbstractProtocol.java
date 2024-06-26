package io.virtue.rpc.protocol;

import io.virtue.common.constant.Key;
import io.virtue.common.constant.Platform;
import io.virtue.common.exception.ResourceException;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.extension.spi.LoadedListener;
import io.virtue.common.url.URL;
import io.virtue.common.util.DateUtil;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.Invocation;
import io.virtue.core.Virtue;
import io.virtue.rpc.event.SendMessageEvent;
import io.virtue.rpc.handler.BaseClientChannelHandlerChain;
import io.virtue.rpc.handler.BaseServerChannelHandlerChain;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.Transporter;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.server.Server;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Abstract Protocol.
 *
 * @param <Req>  request type
 * @param <Resp> response type
 */
@Accessors(fluent = true)
public abstract class AbstractProtocol<Req, Resp> implements Protocol {

    protected static final String SERVER_INVOKE_EXCEPTION = "Server reflect exception: ";

    protected static final String SERVER_EXCEPTION = "Server exception: ";

    protected Endpoints endpoints;

    protected String protocol;

    protected Codec serverCodec;

    protected Codec clientCodec;

    protected ChannelHandler clientHandler;

    protected ChannelHandler serverHandler;

    protected Transporter transporter;

    protected Virtue virtue;

    protected AbstractProtocol(String protocol) {
        this(protocol, new MultiplexEndpoints());
    }

    protected AbstractProtocol(String protocol, Endpoints endpoints) {
        this(protocol, endpoints, null, null);
    }

    protected AbstractProtocol(String protocol, Endpoints endpoints, Codec serverCodec, Codec clientCodec) {
        this(protocol, endpoints, serverCodec, clientCodec, new BaseClientChannelHandlerChain(), new BaseServerChannelHandlerChain());
    }

    protected AbstractProtocol(String protocol, Endpoints endpoints, Codec serverCodec, Codec clientCodec,
                               ChannelHandler clientHandler, ChannelHandler serverHandler) {
        this.protocol = protocol;
        this.endpoints = endpoints;
        this.serverCodec = serverCodec;
        this.clientCodec = clientCodec;
        this.clientHandler = clientHandler;
        this.serverHandler = serverHandler;
    }

    /**
     * Set virtue by {@link LoadedListener}.
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
        return endpoints.acquireClient(url, () -> transporter.connect(url, clientHandler, clientCodec));
    }

    @Override
    public Server openServer(URL url) {
        return endpoints.acquireServer(url, () -> transporter.bind(url, serverHandler, serverCodec));
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
            if (!Platform.isJvmShuttingDown()) {
                doSendRequest(future, request);
            } else {
                RpcFuture.removeFuture(future.id());
            }
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
    public String protocol() {
        return protocol;
    }

    @Override
    public Endpoints endpoints() {
        return endpoints;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Invocation parseOfRequest(Request request) {
        URL url = request.url();
        Virtue virtue = Virtue.ofServer(url);
        Callee<?> callee = virtue.configManager().remoteServiceManager().getCallee(url);
        if (callee == null) {
            throw new ResourceException("Can't find Callee['" + url.path() + "']");
        }
        Req message;
        try {
            message = (Req) request.message();
        } catch (Exception e) {
            throw new UnsupportedOperationException(simpleClassName(this) + " unsupported parse request message type: " + simpleClassName(request.message()));
        }
        Object[] args = parseToInvokeArgs(request, message, callee);
        return createInvocation(url, callee, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object parseOfResponse(Response response) {
        long id = response.id();
        RpcFuture future = RpcFuture.getFuture(id);
        if (future != null) {
            Caller<?> caller = (Caller<?>) future.invocation().invoker();
            Resp message;
            try {
                message = (Resp) response.message();
            } catch (Exception e) {
                throw new UnsupportedOperationException(simpleClassName(this) + "unsupported parse response message type: " + simpleClassName(response.message()));
            }
            return parseToReturnValue(response, message, caller);
        }
        return null;
    }

    protected Transporter loadTransporter(String transport) {
        return ExtensionLoader.loadExtension(Transporter.class, transport);
    }

    protected abstract Object[] parseToInvokeArgs(Request request, Req message, Callee<?> callee);

    protected abstract Object parseToReturnValue(Response response, Resp message, Caller<?> caller);

    protected abstract void doSendRequest(RpcFuture future, Req request);

    protected abstract void doSendResponse(Channel channel, Resp response);

    protected abstract Req createRequest(Invocation invocation);

    protected abstract Resp createResponse(Invocation invocation, Object result);

    protected abstract Resp createResponse(URL url, Throwable cause);

    private Client getClient(Invocation invocation) {
        Caller<?> caller = (Caller<?>) invocation.invoker();
        URL url = caller.clientConfigUrl().replicate();
        url.address(invocation.url().address());
        return openClient(url);
    }
}
