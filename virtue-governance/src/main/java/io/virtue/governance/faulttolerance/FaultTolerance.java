package io.virtue.governance.faulttolerance;

import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.spi.Extensible;
import io.virtue.core.Invocation;

import static io.virtue.common.constant.Components.FaultTolerance.FAIL_FAST;

/**
 * Represents a fault tolerance interface.
 */
@Extensible(FAIL_FAST)
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

