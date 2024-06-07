package io.virtue.rpc.event.listener;

import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.event.EventListener;
import io.virtue.rpc.event.ClientHandlerExceptionEvent;
import io.virtue.transport.RpcFuture;
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
        logger.error("Client: {} exception: {}", event.channel(), cause.getMessage());
        if (url != null) {
            String id = url.getParam(Key.UNIQUE_ID);
            if (!StringUtil.isBlank(id)) {
                RpcFuture future = RpcFuture.getFuture(Long.parseLong(id));
                // if timeout the future will is null
                if (future != null) {
                    future.completeExceptionally(event.source());
                }
            }
        }
    }

}
