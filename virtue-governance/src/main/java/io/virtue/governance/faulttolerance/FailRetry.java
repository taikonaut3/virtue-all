package io.virtue.governance.faulttolerance;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.metrics.CallerMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.virtue.common.constant.Components.FaultTolerance.FAIL_RETRY;

/**
 * FailRetry when an exception occurs in the RPC call.
 */
@Extension(FAIL_RETRY)
public class FailRetry extends AbstractFaultTolerance {

    private static final Logger logger = LoggerFactory.getLogger(FailRetry.class);

    @Override
    public Object doOperation(Invocation invocation) throws RpcException {
        URL url = invocation.url();
        int retries = url.getIntParam(Key.RETRIES, Constant.DEFAULT_RETIRES);
        for (int start = 0; start <= retries; start++) {
            try {
                return invocation.invoke();
            } catch (Exception e) {
                logger.error("An exception occurred in the calling service: " + e.getMessage() + ",Start retry: " + start, e);
                CallerMetrics callerMetrics = invocation.invoker().get(CallerMetrics.ATTRIBUTE_KEY);
                callerMetrics.retryCount().increment();
            }
        }
        throw new RpcException("An exception occurred in the calling service,Retry times: " + retries);
    }

}
