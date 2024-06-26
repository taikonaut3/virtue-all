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
     * Set fallBacker.
     *
     * @param fallBacker
     */
    void fallBacker(T fallBacker);

    /**
     * Current interface's fallBacker.
     *
     * @return
     */
    T fallBacker();

    /**
     * If Rpc call failed,then reflect fallBacker.
     *
     * @param invocation
     * @return
     */
    Object invokeFallBack(Invocation invocation);
}

