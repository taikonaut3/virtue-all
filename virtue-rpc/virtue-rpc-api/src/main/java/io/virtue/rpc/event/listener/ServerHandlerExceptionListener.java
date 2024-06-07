package io.virtue.rpc.event.listener;

import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.event.EventListener;
import io.virtue.rpc.event.ServerHandlerExceptionEvent;
import io.virtue.rpc.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServerHandlerExceptionListener.
 */
public class ServerHandlerExceptionListener implements EventListener<ServerHandlerExceptionEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandlerExceptionListener.class);

    @Override
    public void onEvent(ServerHandlerExceptionEvent event) {
        URL url = event.channel().get(URL.ATTRIBUTE_KEY);
        Throwable cause = event.source();
        logger.error("Server: {} exception: {}", event.channel(), cause.getMessage());
        if (url != null) {
            Protocol protocol = ExtensionLoader.loadExtension(Protocol.class, url.protocol());
            protocol.sendResponse(event.channel(), url, cause);
        }
    }

}
