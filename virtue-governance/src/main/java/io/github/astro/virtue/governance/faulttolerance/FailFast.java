package io.github.astro.virtue.governance.faulttolerance;

import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.config.Invocation;

import static io.github.astro.virtue.common.constant.Components.FaultTolerance.FAIL_FAST;

@ServiceProvider(FAIL_FAST)
public class FailFast extends AbstractFaultTolerance{
    @Override
    protected Object doOperation(Invocation invocation) throws RpcException {
        try {
            return invocation.invoke();
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }
}
