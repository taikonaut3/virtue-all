package io.github.astro.virtue.rpc.listener;

import io.github.astro.virtue.rpc.event.ServerHandlerExceptionEvent;
import io.github.astro.virtue.rpc.protocol.Protocol;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.event.EventListener;
import io.github.astro.virtue.transport.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author WenBo Zhou
 * @Date 2023/12/5 15:49
 */
public class ServerHandlerExceptionListener implements EventListener<ServerHandlerExceptionEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandlerExceptionListener.class);

    @Override
    public void onEvent(ServerHandlerExceptionEvent event) {
        URL url = (URL) event.getChannel().getAttribute(Key.URL);
        Throwable cause = event.source();
        logger.error("Server: {} Exception: {}", event.getChannel(), cause.getMessage());
        Protocol<?,?> protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        Object message = protocol.createResponse(url, cause.getMessage());
        Response response = new Response(url, message);
        response.code(Response.ERROR);
        response.id(Long.valueOf(url.getParameter(Key.UNIQUE_ID)));
        event.getChannel().send(response);
    }

}
