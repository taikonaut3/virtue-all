package io.virtue.governance.faulttolerance;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;

import java.util.concurrent.TimeoutException;

import static io.virtue.common.constant.Components.FaultTolerance.TIMEOUT_RETRY;

/**
 * TimeoutRetry when an exception occurs in the RPC call.
 */
@Extension(TIMEOUT_RETRY)
public class TimeoutRetry extends AbstractFaultTolerance {
    @Override
    protected Object doOperation(Invocation invocation) throws RpcException {
        URL url = invocation.url();
        int retries = url.getIntParam(Key.RETRIES, Constant.DEFAULT_RETIRES);
        for (int start = 0; start <= retries; start++) {
            try {
                return invocation.invoke();
            } catch (Exception e) {
                if (!(e.getCause() instanceof TimeoutException)) {
                    throw RpcException.unwrap(e);
                }
            }
        }
        throw new RpcException("An TimeoutException occurred in the calling service,Retry times: " + retries);
    }
}
