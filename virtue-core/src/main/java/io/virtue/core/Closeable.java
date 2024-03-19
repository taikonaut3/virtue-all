package io.virtue.core;

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
