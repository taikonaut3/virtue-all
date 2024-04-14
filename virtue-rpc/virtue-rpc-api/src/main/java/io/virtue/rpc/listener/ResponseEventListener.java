package io.virtue.rpc.listener;

import io.virtue.common.exception.RpcException;
import io.virtue.common.executor.RpcThreadPool;
import io.virtue.transport.RpcFuture;
import io.virtue.rpc.event.ResponseEvent;
import io.virtue.transport.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * Responsible for handling the response event.
 */
public class ResponseEventListener extends EnvelopeEventListener<ResponseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ResponseEventListener.class);

    public ResponseEventListener() {
        super(RpcThreadPool.defaultCPUExecutor("ResponseListener"));
    }

    @Override
    protected void handEnvelopeEvent(ResponseEvent event) {
        logger.debug("Received Event({})", event.getClass().getSimpleName());
        long id = event.source().id();
        RpcFuture future = RpcFuture.getFuture(id);
        Response response = event.source();
        if (future != null) {
            future.response(response);
            if (response.code() == Response.ERROR) {
                Object body = event.body();
                future.completeExceptionally(new RpcException(String.valueOf(body)));
            } else if (response.code() == Response.SUCCESS) {
                Type returnType = future.returnType();
                if (returnType == response.getClass()) {
                    future.complete(response);
                } else if (returnType == response.message().getClass()) {
                    future.complete(response.message());
                } else {
                    future.complete(event.body());
                }
            }
        } else {
            logger.error("ResponseFuture({}) Can't exist", id);
        }
    }

}
