package io.virtue.registry;

import io.virtue.common.spi.ServiceInterface;
import io.virtue.common.url.ServiceFactory;
import io.virtue.common.constant.Components;

/**
 * Represents a registry factory for creating registry instances.
 */
@ServiceInterface(Components.Registry.CONSUL)
public interface RegistryFactory extends ServiceFactory<Registry> {

}


