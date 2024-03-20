package io.virtue.core;

import io.virtue.common.spi.ServiceInterface;
import io.virtue.core.manager.Virtue;

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