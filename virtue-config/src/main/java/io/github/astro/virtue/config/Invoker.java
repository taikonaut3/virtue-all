package io.github.astro.virtue.config;

import io.github.astro.virtue.common.exception.RpcException;

/**
 * Local invoker.
 */
public interface Invoker<T> {

    /**
     * Invokes the method call with the specified invocation.
     *
     * @param invocation the invocation object containing the method call details
     * @return the result of Invoker
     * @throws RpcException if there is an error during the method invocation
     */
    T invoke(Invocation invocation) throws RpcException;

}

