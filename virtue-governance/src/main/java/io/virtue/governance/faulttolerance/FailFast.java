package io.virtue.governance.faulttolerance;

import io.virtue.common.exception.RpcException;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.core.Invocation;

import static io.virtue.common.constant.Components.FaultTolerance.FAIL_FAST;

/**
 * FailFast when an exception occurs in the RPC call.
 */
@ServiceProvider(FAIL_FAST)
public class FailFast extends AbstractFaultTolerance {
    @Override
    protected Object doOperation(Invocation invocation) throws RpcException {
        try {
            return invocation.invoke();
        } catch (Exception e) {
            throw RpcException.unwrap(e);
        }
    }
}
