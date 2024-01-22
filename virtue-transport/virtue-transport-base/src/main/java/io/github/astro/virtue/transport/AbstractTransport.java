package io.github.astro.virtue.transport;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.exception.NetWorkException;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.channel.ChannelHandler;
import io.github.astro.virtue.transport.client.Client;
import io.github.astro.virtue.transport.code.Codec;
import io.github.astro.virtue.transport.server.Server;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author WenBo Zhou
 * @Date 2023/12/4 16:49
 */
public abstract class AbstractTransport implements Transporter {

    private final Map<String, Client> multiplexClients = new ConcurrentHashMap<>();

    private final Map<String, Client> customClients = new ConcurrentHashMap<>();

    private final Map<String, Server> servers = new ConcurrentHashMap<>();

    @Override
    public Server bind(URL url, ChannelHandler handler, Codec codec) throws NetWorkException {
        String key = url.authority();
        Server server = servers.get(key);
        if (server == null) {
            synchronized (this) {
                if (servers.get(key) == null) {
                    server = createServer(url, handler, codec);
                    servers.put(key, server);
                }
            }
        } else if (!server.isActive()) {
            server.bind();
        }
        return server;
    }

    @Override
    public Client connect(URL url, ChannelHandler handler, Codec codec) throws NetWorkException {
        boolean isMultiplex = url.getBooleanParameter(Key.MULTIPLEX, false);
        Client client;
        if (isMultiplex) {
            String key = url.authority();
            client = getClient(url, handler, codec, key, multiplexClients);
        } else {
            String key = url.uri();
            client = getClient(url, handler, codec, key, customClients);
        }
        return client;
    }

    protected abstract Client createClient(URL url, ChannelHandler handler, Codec codec);

    protected abstract Server createServer(URL url, ChannelHandler handler, Codec codec);

    public Server[] getServers() {
        return servers.values().toArray(Server[]::new);
    }

    public Client[] getClients() {
        LinkedList<Client> clients = new LinkedList<>(multiplexClients.values());
        clients.addAll(customClients.values());
        return clients.toArray(Client[]::new);
    }

    private Client getClient(URL url, ChannelHandler handler, Codec codec, String key, Map<String, Client> clients) {
        Client client;
        client = clients.get(key);
        if (client == null) {
            synchronized (this) {
                if (clients.get(key) == null) {
                    client = createClient(url, handler, codec);
                    clients.put(key, client);
                }
            }
        } else if (!client.isActive()) {
            client.connect();
        }
        return client;
    }

}
