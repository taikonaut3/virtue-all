package io.virtue.rpc.protocol;

import io.virtue.common.url.URL;
import io.virtue.core.Closeable;
import io.virtue.transport.client.Client;
import io.virtue.transport.server.Server;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Management endpoints,get client and server.
 */
public interface Endpoints extends Closeable {

    /**
     * Get client by remoteUrl, if it doesn't exist, create it.
     *
     * @param remoteUrl
     * @param createFunction
     * @return
     */
    Client getClient(URL remoteUrl, Supplier<Client> createFunction);

    /**
     * Get server by url, if it doesn't exist, create it.
     *
     * @param url
     * @param createFunction
     * @return
     */
    Server getServer(URL url, Supplier<Server> createFunction);

    /**
     * Get clients.
     *
     * @return
     */
    Collection<Client> clients();

    /**
     * Get servers.
     *
     * @return
     */
    Collection<Server> servers();
}
