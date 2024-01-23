package io.github.astro.virtue.rpc.initialize;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.config.Virtue;
import io.github.astro.virtue.config.VirtueConfiguration;
import io.github.astro.virtue.event.EventDispatcher;
import io.github.astro.virtue.rpc.event.*;
import io.github.astro.virtue.rpc.listener.*;

/**
 * Registry Global Default Listeners
 */
@ServiceProvider
public class InitializerConfiguration implements VirtueConfiguration {

    @Override
    public void initAfter(Virtue application) {
        EventDispatcher eventDispatcher = application.eventDispatcher();
        eventDispatcher.addListener(HeartBeatEvent.class, new HeartBeatEventListener());
        eventDispatcher.addListener(RequestEvent.class, new RequestEventListener());
        eventDispatcher.addListener(ResponseEvent.class, new ResponseEventListener());
        eventDispatcher.addListener(ClientHandlerExceptionEvent.class, new ClientHandlerExceptionListener());
        eventDispatcher.addListener(ServerHandlerExceptionEvent.class, new ServerHandlerExceptionListener());
    }

}
