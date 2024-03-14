package io.virtue.rpc.listener;

import io.virtue.common.constant.Key;
import io.virtue.common.executor.RpcThreadPool;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.common.util.DateUtil;
import io.virtue.config.CallArgs;
import io.virtue.config.ServerCaller;
import io.virtue.rpc.event.RequestEvent;
import io.virtue.rpc.protocol.Protocol;
import io.virtue.rpc.protocol.ProtocolParser;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
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
    protected void handEnvelopeEvent(RequestEvent event) {
        logger.debug("Received Event({})", event.getClass().getSimpleName());
        Request request = event.source();
        URL url = request.url();
        Protocol<?,?> protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.parser();
        CallArgs callArgs = protocolParser.parseRequestBody(request);
        ServerCaller<?> serverCaller = (ServerCaller<?>) callArgs.caller();
        boolean oneway = url.getBooleanParameter(Key.ONEWAY);
        try {
            RpcContext.getContext().attribute(Request.ATTRIBUTE_KEY).set(request);
            if (oneway) {
                serverCaller.call(url,callArgs);
            } else {
                long timeout = url.getLongParameter(Key.TIMEOUT);
                String timestamp = url.getParameter(Key.TIMESTAMP);
                LocalDateTime localDateTime = DateUtil.parse(timestamp, DateUtil.COMPACT_FORMAT);
                long margin = Duration.between(localDateTime, LocalDateTime.now()).toMillis();
                if (margin < timeout) {
                    Object result = serverCaller.call(url,callArgs);
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
