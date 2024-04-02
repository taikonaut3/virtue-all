package io.virtue.common.url;

/**
 * Factory for the SPI instance created from the Url configuration.
 *
 * @param <T> service type
 */
public interface ServiceFactory<T> {

    /**
     * Get the instance by the url.
     *
     * @param url
     * @return instance
     */
    T get(URL url);
}