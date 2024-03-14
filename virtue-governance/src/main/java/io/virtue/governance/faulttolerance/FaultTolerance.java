package io.virtue.governance.faulttolerance;

import io.virtue.common.exception.RpcException;
import io.virtue.common.spi.ServiceInterface;
import io.virtue.config.Invocation;

import static io.virtue.common.constant.Components.FaultTolerance.FAIL_RETRY;

/**
 * Represents a fault tolerance interface.
 */
@ServiceInterface(FAIL_RETRY)
public interface FaultTolerance {

    /**
     * Performs an operation with fault tolerance.
     *
     * @param invocation the invocation to be performed.
     * @return use invocation.invoke()
     * @throws RpcException
     */
    Object operation(Invocation invocation) throws RpcException;
}

