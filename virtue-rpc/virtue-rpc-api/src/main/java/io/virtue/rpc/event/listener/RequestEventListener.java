package io.virtue.rpc.event.listener;

import io.virtue.common.exception.RpcException;
import io.virtue.common.executor.RpcThreadPool;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Invocation;
import io.virtue.rpc.event.RequestEvent;
import io.virtue.rpc.protocol.Protocol;
import io.virtue.transport.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Receive request support and reflect the service.
 */
public class RequestEventListener extends EnvelopeEventListener<RequestEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RequestEventListener.class);

    public RequestEventListener() {
        super(RpcThreadPool.defaultIOExecutor("request-handler"));
    }

    @Override
    protected void handEnvelopeEvent(RequestEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Received <{}>", simpleClassName(event));
        }
        Request request = event.source();
        URL url = request.url();
        RpcContext.currentContext().set(Request.ATTRIBUTE_KEY, request);
        RpcContext.RequestContext.parse(url);
        Invocation invocation = event.invocation();
        Callee<?> callee = (Callee<?>) invocation.invoker();
        callee.invoke(invocation);

    }

    @Override
    protected void jvmShuttingDown(RequestEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Received <{}> but jvm is shutting down", simpleClassName(event));
        }
        Protocol protocol = event.protocol();
        RpcException e = new RpcException("Server closing and no longer processing requests");
        protocol.sendResponse(event.channel(), event.source().url(), e);
    }
}
