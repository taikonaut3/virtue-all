package io.github.astro.virtue.common.spi;

/**
 * Listen for, when the Service instantiation is Created.
 *
 * @param <T>
 */
@FunctionalInterface
public interface LoadedListener<T> {

    /**
     * When the service is created, call this method.
     *
     * @param service
     */
    void listen(T service);

}
