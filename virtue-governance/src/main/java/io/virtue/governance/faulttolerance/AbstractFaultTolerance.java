package io.virtue.governance.faulttolerance;

import io.virtue.common.exception.RpcException;
import io.virtue.core.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFaultTolerance implements FaultTolerance {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFaultTolerance.class);

    @Override
    public Object operation(Invocation invocation) throws RpcException {
        logger.debug("Used FaultTolerance: {}", this.getClass().getSimpleName());
        // todo 限流、熔断
        return doOperation(invocation);
    }

    // 容错机制
    protected abstract Object doOperation(Invocation invocation) throws RpcException;

}
