package io.virtue.core;

import io.virtue.common.spi.ServiceInterface;

/**
 * Ability to scale over the life cycle of Virtue.
 */
@ServiceInterface(lazyLoad = false)
public interface VirtueConfiguration {

    /**
     * Virtue init before.
     *
     * @param virtue
     */
    default void initBefore(Virtue virtue) {

    }

    /**
     * Virtue init after.
     *
     * @param virtue
     */
    default void initAfter(Virtue virtue) {

    }

    /**
     * Virtue start before.
     *
     * @param virtue
     */
    default void startBefore(Virtue virtue) {

    }

    /**
     * Virtue start after.
     *
     * @param virtue
     */
    default void startAfter(Virtue virtue) {

    }

    /**
     * Virtue stop before.
     *
     * @param virtue
     */
    default void stopBefore(Virtue virtue) {
    }

    /**
     * Virtue stop after.
     *
     * @param virtue
     */
    default void stopAfter(Virtue virtue) {
    }

}
