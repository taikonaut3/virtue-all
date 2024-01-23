package io.github.astro.virtue.event;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.url.ServiceFactory;

import static io.github.astro.virtue.common.constant.Components.EventDispatcher.DISRUPTOR;

/**
 * Factory for creating event dispatchers.
 */
@ServiceInterface(DISRUPTOR)
public interface EventDispatcherFactory extends ServiceFactory<EventDispatcher> {

}

