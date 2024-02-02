package io.github.astro.virtue.config;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.config.manager.Virtue;

@ServiceInterface(lazyLoad = false)
public interface VirtueConfiguration {

    default void initBefore(Virtue virtue) {

    }

    default void initAfter(Virtue virtue) {

    }

    default void startBefore(Virtue virtue) {

    }

    default void startAfter(Virtue virtue) {

    }

    default void stopBefore(Virtue virtue) {
    }

    default void stopAfter(Virtue virtue) {
    }

}
