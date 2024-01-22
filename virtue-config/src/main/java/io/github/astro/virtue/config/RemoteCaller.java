package io.github.astro.virtue.config;

/**
 * Remote caller for invoking remote service methods.
 *
 * @param <T> The type of the remote service interface.
 */
public interface RemoteCaller<T> extends CallerContainer {

    /**
     * Gets the target interface of the remote service.
     *
     * @return The target interface.
     */
    Class<T> targetInterface();

    /**
     * Gets a Proxy instance of the remote service.
     *
     * @return The remote service instance.
     */
    T get();

}

