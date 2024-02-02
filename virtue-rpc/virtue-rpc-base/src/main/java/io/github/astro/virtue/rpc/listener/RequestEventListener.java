package io.github.astro.virtue.rpc.listener;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.executor.RpcThreadPool;
import io.github.astro.virtue.common.extension.RpcContext;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.DateUtil;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.config.Invoker;
import io.github.astro.virtue.config.ServerCaller;
import io.github.astro.virtue.rpc.event.EnvelopeEventListener;
import io.github.astro.virtue.rpc.event.RequestEvent;
import io.github.astro.virtue.rpc.protocol.Protocol;
import io.github.astro.virtue.rpc.protocol.ProtocolParser;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

public class RequestEventListener extends EnvelopeEventListener<RequestEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RequestEventListener.class);

    public RequestEventListener() {
        super(RpcThreadPool.defaultIOExecutor("handle-request"));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handEnvelopeEvent(RequestEvent event) {
        logger.debug("Received Event({})", event.getClass().getSimpleName());
        Request request = event.source();
        URL url = request.url();
        Protocol<?,?> protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.parser();
        CallArgs callArgs = protocolParser.parseRequestBody(request);
        ServerCaller<?> serverCaller = (ServerCaller<?>) callArgs.caller();
        Invocation invocation = Invocation.create(url, callArgs, inv -> serverCaller.call(callArgs));
        Invoker<Object> invoker = (Invoker<Object>) serverCaller.invoker();
        boolean oneway = url.getBooleanParameter(Key.ONEWAY);
        try {
            RpcContext.getContext().attribute(Request.ATTRIBUTE_KEY).set(request);
            if (oneway) {
                invoker.invoke(invocation);
            } else {
                long timeout = url.getLongParameter(Key.TIMEOUT);
                String timestamp = url.getParameter(Key.TIMESTAMP);
                LocalDateTime localDateTime = DateUtil.parse(timestamp, DateUtil.COMPACT_FORMAT);
                long margin = Duration.between(localDateTime, LocalDateTime.now()).toMillis();
                if (margin < timeout) {
                    Object result = invoker.invoke(invocation);
                    long invokeAfterMargin = Duration.between(localDateTime, LocalDateTime.now()).toMillis();
                    if (invokeAfterMargin < timeout) {
                        Response response = new Response(url, result);
                        response.code(Response.SUCCESS);
                        event.getChannel().send(response);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Invoke " + url.path() + " fail", e);
            Object message = protocol.createResponse(url, e.getMessage());
            Response response = new Response(url, message);
            response.code(Response.ERROR);
            event.getChannel().send(response);
        }
    }
}
