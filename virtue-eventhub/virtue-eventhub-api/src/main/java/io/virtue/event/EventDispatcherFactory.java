package io.virtue.event;

import io.virtue.common.spi.ServiceInterface;
import io.virtue.common.url.ServiceFactory;
import io.virtue.common.constant.Components;

/**
 * Factory for creating event dispatchers.
 */
@ServiceInterface(Components.EventDispatcher.DISRUPTOR)
public interface EventDispatcherFactory extends ServiceFactory<EventDispatcher> {

}

