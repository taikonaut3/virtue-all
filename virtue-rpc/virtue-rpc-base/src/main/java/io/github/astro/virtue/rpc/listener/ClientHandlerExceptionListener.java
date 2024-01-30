package io.github.astro.virtue.rpc.listener;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.event.EventListener;
import io.github.astro.virtue.rpc.event.ClientHandlerExceptionEvent;
import io.github.astro.virtue.transport.RpcFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author WenBo Zhou
 * @Date 2023/12/5 15:49
 */
public class ClientHandlerExceptionListener implements EventListener<ClientHandlerExceptionEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandlerExceptionListener.class);

    @Override
    public void onEvent(ClientHandlerExceptionEvent event) {
        URL url = (URL) event.getChannel().getAttribute(Key.URL);
        Throwable cause = event.source();
        logger.error("Client: {} Exception: {}", event.getChannel(), cause.getMessage());
        if(url!=null){
            RpcFuture future = RpcFuture.getFuture(url.getParameter(Key.UNIQUE_ID));
            // if timeout the future will is null
            if (future != null) {
                future.completeExceptionally(event.source());
            }
        }
    }

}
