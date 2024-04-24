package io.virtue.rpc.listener;

import io.virtue.common.executor.RpcThreadPool;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Invocation;
import io.virtue.rpc.event.RequestEvent;
import io.virtue.transport.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Receive request event and invoke the service.
 */
public class RequestEventListener extends EnvelopeEventListener<RequestEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RequestEventListener.class);

    public RequestEventListener() {
        super(RpcThreadPool.defaultIOExecutor("RequestListener"));
    }

    @Override
    protected void handEnvelopeEvent(RequestEvent event) {
        logger.debug("Received Event({})", simpleClassName(event));
        Request request = event.source();
        URL url = request.url();
        RpcContext.currentContext().set(Request.ATTRIBUTE_KEY, request);
        RpcContext.RequestContext.parse(url);
        Invocation invocation = event.invocation();
        Callee<?> callee = (Callee<?>) invocation.invoker();
        callee.invoke(invocation);
    }
}
