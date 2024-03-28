package io.virtue.registry;

import io.virtue.common.url.URL;
import io.virtue.core.Closeable;

import java.util.List;

/**
 * RegistryService For service registration and discovery.
 */
public interface RegistryService extends Closeable {


    /**
     * Connects to the registry using the specified URL.
     *
     * @param url The URL of the registry to connect to.
     */
    void connect(URL url);

    /**
     * Registers the specified URL with the registry.
     *
     * @param url The URL to register.
     */
    void register(URL url);

    /**
     * Discovers services with the specified URL from the registry.
     *
     * @param url The URL used for service discovery.
     * @return The discovered services.
     */
    List<URL> discover(URL url);

}

