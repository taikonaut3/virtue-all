package io.virtue.governance.faulttolerance;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.config.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.virtue.common.constant.Components.FaultTolerance.FAIL_RETRY;

@ServiceProvider(FAIL_RETRY)
public class FailRetry extends AbstractFaultTolerance {

    private static final Logger logger = LoggerFactory.getLogger(FailRetry.class);

    @Override
    public Object doOperation(Invocation invocation) throws RpcException {
        URL url = invocation.url();
        int retries = Integer.parseInt(url.getParameter(Key.RETRIES, String.valueOf(Constant.DEFAULT_RETIRES)));
        for (int start = 0; start <= retries; start++) {
            try {
                return invocation.invoke();
            } catch (Exception e) {
                logger.error("An exception occurred in the calling service: " + e.getMessage() + ",Start retry: " + start , e);
            }
        }
        throw new RpcException("An exception occurred in the calling service,Retry times: " + retries);
    }

}
