package io.virtue.governance.faulttolerance;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.spi.Extensible;
import io.virtue.core.Invocation;

import static io.virtue.common.constant.Components.FaultTolerance.FAIL_FAST;

/**
 * Represents a fault tolerance interface.
 */
@Extensible(value = FAIL_FAST, key = Key.FAULT_TOLERANCE)
public interface FaultTolerance {

    /**
     * Performs an operation with fault tolerance.
     *
     * @param invocation the invocation to be performed.
     * @return use invocation.reflect()
     * @throws RpcException
     */
    Object operation(Invocation invocation) throws RpcException;
}

