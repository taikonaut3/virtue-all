package io.github.astro.virtue.governance.faulttolerance;

import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.config.Invocation;

import static io.github.astro.virtue.common.constant.Components.FaultTolerance.FAIL_RETRY;

/**
 * Represents a fault tolerance interface.
 */
@ServiceInterface(FAIL_RETRY)
public interface FaultTolerance {

    /**
     * Performs an operation with fault tolerance.
     *
     * @param invocation The invocation to be performed.
     * @return The result of the operation.
     * @throws RpcException If an error occurs during the operation.
     */
    Object operation(Invocation invocation) throws RpcException;
}

