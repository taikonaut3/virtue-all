package io.virtue.core;

import java.net.InetSocketAddress;

/**
 * Remote caller for invoking remote service methods.
 *
 * @param <T> The type of the remote service interface.
 */
public interface RemoteCaller<T> extends InvokerContainer {

    /**
     * Gets the target interface of the remote service.
     *
     * @return the target interface
     */
    Class<T> targetInterface();

    /**
     * Gets a Proxy instance of the remote service.
     *
     * @return proxy instance
     */
    T get();

    /**
     * Is it only when the first call is made that the registration center is actually connected to Get the available services.
     * The default gets available services when {@link Caller} creation is complete.
     */
    boolean lazyDiscover();

    /**
     * Direct url address.
     */
    InetSocketAddress directAddress();

    /**
     * Gets the caller for the specified protocol and path.
     *
     * @param protocol
     * @param path
     * @return
     */
    default Caller<?> getCaller(String protocol, String path) {
        return (Caller<?>) getInvoker(protocol, path);
    }
}

