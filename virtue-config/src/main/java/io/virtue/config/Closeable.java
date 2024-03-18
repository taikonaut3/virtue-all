package io.virtue.config;

import io.virtue.common.exception.RpcException;

public interface Closeable {

    /**
     * Close Resource
     *
     * @throws RpcException
     */
    void close() throws RpcException;

    /**
     * Whether the Resource is Active
     *
     * @return
     */
    boolean isActive();

}
