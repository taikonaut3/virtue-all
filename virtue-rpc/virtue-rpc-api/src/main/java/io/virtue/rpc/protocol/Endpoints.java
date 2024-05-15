package io.virtue.rpc.protocol;

import io.virtue.common.extension.resoruce.Cleanable;
import io.virtue.common.url.URL;
import io.virtue.transport.client.Client;
import io.virtue.transport.server.Server;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Management endpoints,get client and server.
 */
public interface Endpoints extends Cleanable {

    /**
     * Get client by remoteUrl, if it doesn't exist, create it.
     *
     * @param remoteUrl
     * @param createFunction
     * @return
     */
    Client acquireClient(URL remoteUrl, Supplier<Client> createFunction);

    /**
     * Get server by url, if it doesn't exist, create it.
     *
     * @param url
     * @param createFunction
     * @return
     */
    Server acquireServer(URL url, Supplier<Server> createFunction);

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
