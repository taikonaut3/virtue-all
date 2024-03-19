package io.virtue.core;

import io.virtue.common.exception.RpcException;

/**
 * Direct remote call.
 */
@FunctionalInterface
public interface DirectRemoteCall {

    /**
     * Makes a direct remote service call.
     *
     * @param invocation The invocation to be called.
     * @return The result of the remote service call.
     * @throws RpcException If an error occurs during the remote service call.
     */
    Object call(Invocation invocation) throws RpcException;

}

