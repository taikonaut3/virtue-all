package io.virtue.transport;

import io.virtue.common.exception.NetWorkException;

public interface Closeable {

    /**
     * Close Resource
     *
     * @throws NetWorkException
     */
    void close() throws NetWorkException;

    /**
     * Whether the Current Channel is Active (can read or write)
     * for processing when the channel is inactive
     *
     * @return
     */
    boolean isActive();

}
