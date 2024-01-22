package io.github.astro.virtue.event;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.url.URL;

import static io.github.astro.virtue.common.constant.Components.EventDispatcher.DISRUPTOR;

/**
 * Factory for creating event dispatchers.
 */
@ServiceInterface(DISRUPTOR)
public interface EventDispatcherFactory {

    /**
     * Creates an event dispatcher for the specified URL.
     *
     * @param url The URL for creating the event dispatcher.
     * @return The created event dispatcher.
     */
    EventDispatcher create(URL url);
}

