package io.github.astro.rpc.listener;

import io.github.astro.rpc.event.EnvelopeEventListener;
import io.github.astro.rpc.event.ResponseEvent;
import io.github.astro.virtue.common.executor.RpcThreadPool;
import io.github.astro.virtue.transport.Response;
import io.github.astro.virtue.transport.ResponseFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class ResponseEventListener extends EnvelopeEventListener<ResponseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ResponseEventListener.class);

    public ResponseEventListener() {
        super(RpcThreadPool.defaultCPUExecutor("HandleResponse"));
    }

    @Override
    protected void handEnvelopeEvent(ResponseEvent event) {
        logger.debug("Received Event({})", event.getClass().getSimpleName());
        String id = String.valueOf(event.source().id());
        ResponseFuture future = ResponseFuture.getFuture(id);
        Response response = event.source();
        if (future != null) {
            future.setResponse(response);
            Type returnType = future.returnType();
            if (returnType == response.getClass()) {
                future.complete(response);
            } else if (returnType == response.message().getClass()) {
                future.complete(response.message());
            } else {
                future.complete(event.getBody());
            }
        } else {
            logger.error("ResponseFuture({}) Can't exist", id);
        }
    }

}
