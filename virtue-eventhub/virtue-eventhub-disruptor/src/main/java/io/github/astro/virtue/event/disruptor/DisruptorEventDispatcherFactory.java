package io.github.astro.virtue.event.disruptor;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.SingleServiceFactory;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.event.EventDispatcher;
import io.github.astro.virtue.event.EventDispatcherFactory;

import static io.github.astro.virtue.common.constant.Components.EventDispatcher.DISRUPTOR;

/**
 * DisruptorEventDispatcherFactory
 */
@ServiceProvider(DISRUPTOR)
public class DisruptorEventDispatcherFactory extends SingleServiceFactory<EventDispatcher> implements EventDispatcherFactory {

    @Override
    protected EventDispatcher create(URL url) {
        return new DisruptorEventDispatcher(url);
    }
}
