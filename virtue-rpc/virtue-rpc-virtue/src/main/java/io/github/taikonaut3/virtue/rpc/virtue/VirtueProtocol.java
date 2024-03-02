package io.github.taikonaut3.virtue.rpc.virtue;

import io.github.taikonaut3.virtue.common.constant.Key;
import io.github.taikonaut3.virtue.common.spi.ServiceProvider;
import io.github.taikonaut3.virtue.common.url.URL;
import io.github.taikonaut3.virtue.config.CallArgs;
import io.github.taikonaut3.virtue.rpc.protocol.AbstractProtocol;
import io.github.taikonaut3.virtue.rpc.virtue.client.VirtueClientChannelHandlerChain;
import io.github.taikonaut3.virtue.rpc.virtue.envelope.VirtueRequest;
import io.github.taikonaut3.virtue.rpc.virtue.envelope.VirtueResponse;
import io.github.taikonaut3.virtue.rpc.virtue.server.VirtueServerChannelHandlerChain;
import io.github.taikonaut3.virtue.transport.channel.ChannelHandler;
import io.github.taikonaut3.virtue.transport.client.Client;
import io.github.taikonaut3.virtue.transport.codec.Codec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.taikonaut3.virtue.common.constant.Components.Protocol.VIRTUE;

@ServiceProvider(VIRTUE)
public final class VirtueProtocol extends AbstractProtocol<VirtueRequest, VirtueResponse> {

    private final Map<String, Client> multiplexClients = new ConcurrentHashMap<>();

    private final Map<String, Client> customClients = new ConcurrentHashMap<>();

    public VirtueProtocol() {
        super(VIRTUE,
                new VirtueCodec(VirtueResponse.class, VirtueRequest.class),
                new VirtueCodec(VirtueRequest.class, VirtueResponse.class),
                new VirtueClientChannelHandlerChain(),
                new VirtueServerChannelHandlerChain(),
                new VirtueProtocolParser());
    }

    @Override
    public Client openClient(URL url) {
        boolean isMultiplex = url.getBooleanParameter(Key.MULTIPLEX, false);
        Client client;
        if (isMultiplex) {
            String key = url.authority();
            client = getClient(url, clientHandler, clientCodec, key, multiplexClients);
        } else {
            String key = url.uri();
            client = getClient(url, clientHandler, clientCodec, key, customClients);
        }
        return client;
    }

    @Override
    public VirtueRequest createRequest(URL url, CallArgs args) {
        url.addParameter(Key.BODY_TYPE, args.getClass().getName());
        return new VirtueRequest(url, args);
    }

    @Override
    public VirtueResponse createResponse(URL url, Object payload) {
        url.addParameter(Key.BODY_TYPE, payload.getClass().getName());
        return new VirtueResponse(url, payload);
    }

    private Client getClient(URL url, ChannelHandler handler, Codec codec, String key, Map<String, Client> clients) {
        Client client;
        client = clients.get(key);
        if (client == null) {
            synchronized (this) {
                if (clients.get(key) == null) {
                    client = transporter.connect(url, handler, codec);
                    clients.put(key, client);
                }
            }
        } else if (!client.isActive()) {
            client.connect();
        }
        return client;
    }

}
