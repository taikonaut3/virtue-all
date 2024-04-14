package io.virtue.rpc.listener;

import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.event.EventListener;
import io.virtue.transport.RpcFuture;
import io.virtue.rpc.event.ClientHandlerExceptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClientHandlerExceptionListener.
 */
public class ClientHandlerExceptionListener implements EventListener<ClientHandlerExceptionEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandlerExceptionListener.class);

    @Override
    public void onEvent(ClientHandlerExceptionEvent event) {
        URL url = event.channel().get(URL.ATTRIBUTE_KEY);
        Throwable cause = event.source();
        logger.error("Client: {} Exception: {}", event.channel(), cause.getMessage());
        if (url != null) {
            RpcFuture future = RpcFuture.getFuture(url.getLongParam(Key.UNIQUE_ID));
            // if timeout the future will is null
            if (future != null) {
                future.completeExceptionally(event.source());
            }
        }
    }

}
