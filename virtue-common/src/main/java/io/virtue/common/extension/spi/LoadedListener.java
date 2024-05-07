package io.virtue.common.extension.spi;

/**
 * SPI listener, when the Extension instantiation is Created.
 *
 * @param <T> service type
 */
@FunctionalInterface
public interface LoadedListener<T> {

    /**
     * When the extension is created, reflect this method.
     *
     * @param service
     */
    void listen(T service);

}
