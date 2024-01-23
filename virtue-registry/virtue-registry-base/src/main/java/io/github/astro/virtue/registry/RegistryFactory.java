package io.github.astro.virtue.registry;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.url.ServiceFactory;

import static io.github.astro.virtue.common.constant.Components.Registry.CONSUL;

/**
 * Represents a registry factory for creating registry instances.
 */
@ServiceInterface(CONSUL)
public interface RegistryFactory extends ServiceFactory<Registry> {

}


