package io.virtue.rpc.listener;

import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.event.EventListener;
import io.virtue.rpc.event.ServerHandlerExceptionEvent;
import io.virtue.rpc.protocol.Protocol;
import io.virtue.transport.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServerHandlerExceptionListener.
 */
public class ServerHandlerExceptionListener implements EventListener<ServerHandlerExceptionEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandlerExceptionListener.class);

    @Override
    public void onEvent(ServerHandlerExceptionEvent event) {
        URL url = event.channel().attribute(URL.ATTRIBUTE_KEY).get();
        Throwable cause = event.source();
        logger.error("Server: {} Exception: {}", event.channel(), cause.getMessage());
        if (url != null) {
            Protocol<?, ?> protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
            Object message = protocol.createResponse(url, cause.getMessage());
            Response response = Response.error(url, message);
            event.channel().send(response);
        }
    }

}
