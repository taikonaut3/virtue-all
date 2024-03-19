package io.virtue.rpc.virtue;

import io.virtue.common.constant.Components;
import io.virtue.common.constant.Key;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.core.CallArgs;
import io.virtue.rpc.protocol.AbstractProtocol;
import io.virtue.rpc.virtue.client.VirtueClientChannelHandlerChain;
import io.virtue.rpc.virtue.envelope.VirtueRequest;
import io.virtue.rpc.virtue.envelope.VirtueResponse;
import io.virtue.rpc.virtue.server.VirtueServerChannelHandlerChain;
import io.virtue.transport.client.Client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServiceProvider(Components.Protocol.VIRTUE)
public final class VirtueProtocol extends AbstractProtocol<VirtueRequest, VirtueResponse> {

    private final Map<String, Client> multiplexClients = new ConcurrentHashMap<>();

    private final Map<String, Client> customClients = new ConcurrentHashMap<>();

    public VirtueProtocol() {
        super(Components.Protocol.VIRTUE,
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
            client = getClient(url, key, multiplexClients);
        } else {
            String key = url.uri();
            client = getClient(url, key, customClients);
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

    private Client getClient(URL url, String key, Map<String, Client> clients) {
        Client client;
        client = clients.get(key);
        if (client == null) {
            synchronized (this) {
                if (clients.get(key) == null) {
                    client = transporter.connect(url, clientHandler, clientCodec);
                    clients.put(key, client);
                }
            }
        } else if (!client.isActive()) {
            client.connect();
        }
        return client;
    }

}
