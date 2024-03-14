package io.virtue.event.disruptor;

import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.SingleServiceFactory;
import io.virtue.common.url.URL;
import io.virtue.event.EventDispatcher;
import io.virtue.event.EventDispatcherFactory;
import io.virtue.common.constant.Components;

/**
 * DisruptorEventDispatcherFactory
 */
@ServiceProvider(Components.EventDispatcher.DISRUPTOR)
public class DisruptorEventDispatcherFactory extends SingleServiceFactory<EventDispatcher> implements EventDispatcherFactory {

    @Override
    protected EventDispatcher create(URL url) {
        return new DisruptorEventDispatcher(url);
    }
}
