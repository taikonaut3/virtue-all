package io.virtue.rpc.protocol;

import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.client.Client;
import io.virtue.transport.server.Server;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Multiplex Endpoints.
 */
public class MultiplexEndpoints implements Endpoints {

    private final Map<String, Client> multiplexClients = new ConcurrentHashMap<>();
    private final Map<String, Client> customClients = new ConcurrentHashMap<>();
    private final Map<String, Server> servers = new ConcurrentHashMap<>();
    private volatile boolean active = true;

    @Override
    public Client getClient(URL remoteUrl, Supplier<Client> createFunction) {
        boolean isMultiplex = remoteUrl.getBooleanParam(Key.MULTIPLEX, false);
        if (isMultiplex) {
            String key = remoteUrl.authority();
            return getClient(key, multiplexClients, createFunction);
        } else {
            String key = remoteUrl.uri();
            return getClient(key, customClients, createFunction);
        }
    }

    @Override
    public Server getServer(URL url, Supplier<Server> createFunction) {
        return servers.computeIfAbsent(url.authority(), k -> createFunction.get());
    }

    @Override
    public Collection<Client> clients() {
        LinkedList<Client> clients = new LinkedList<>();
        clients.addAll(multiplexClients.values());
        clients.addAll(customClients.values());
        return clients;
    }

    @Override
    public Collection<Server> servers() {
        return servers.values();
    }

    private Client getClient(String key, Map<String, Client> clients, Supplier<Client> createFunction) {
        Client client = clients.computeIfAbsent(key, k -> createFunction.get());
        if (!client.isActive()) {
            client.connect();
        }
        return client;
    }

    @Override
    public synchronized void close() {
        if (active) {
            clients().forEach(Client::close);
            servers().forEach(Server::close);
            customClients.clear();
            multiplexClients.clear();
            servers.clear();
            active = false;
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
