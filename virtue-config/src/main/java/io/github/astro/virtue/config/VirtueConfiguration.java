package io.github.astro.virtue.config;

import io.github.astro.virtue.common.spi.ServiceInterface;

@ServiceInterface(lazyLoad = false)
public interface VirtueConfiguration {

    default void initBefore(Virtue application) {

    }

    default void initAfter(Virtue application) {

    }

    default void startBefore(Virtue application) {

    }

    default void startAfter(Virtue application) {

    }

    default void stopBefore(Virtue application) {
    }

    default void stopAfter(Virtue application) {
    }

}
