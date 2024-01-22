package io.github.astro.virtue.registry;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.url.URL;

import static io.github.astro.virtue.common.constant.Components.Registry.CONSUL;

/**
 * Represents a registry factory for creating registry instances.
 */
@ServiceInterface(CONSUL)
public interface RegistryFactory {

    /**
     * Gets a registry instance for the specified URL.
     *
     * @param url The URL for creating the registry instance.
     * @return The created registry instance.
     */
    Registry getRegistry(URL url);
}


