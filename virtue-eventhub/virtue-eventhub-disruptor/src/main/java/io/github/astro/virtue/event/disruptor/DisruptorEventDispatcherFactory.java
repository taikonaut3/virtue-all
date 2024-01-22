package io.github.astro.virtue.event.disruptor;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.event.EventDispatcher;
import io.github.astro.virtue.event.EventDispatcherFactory;

import static io.github.astro.virtue.common.constant.Components.EventDispatcher.DISRUPTOR;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/7 14:08
 */
@ServiceProvider(DISRUPTOR)
public class DisruptorEventDispatcherFactory implements EventDispatcherFactory {
    @Override
    public EventDispatcher create(URL url) {
        return new DisruptorEventDispatcher(url);
    }
}
