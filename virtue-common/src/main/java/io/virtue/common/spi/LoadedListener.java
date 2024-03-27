package io.virtue.common.spi;

/**
 * SPI listener, when the Service instantiation is Created.
 *
 * @param <T> service type
 */
@FunctionalInterface
public interface LoadedListener<T> {

    /**
     * When the service is created, invoke this method.
     *
     * @param service
     */
    void listen(T service);

}
