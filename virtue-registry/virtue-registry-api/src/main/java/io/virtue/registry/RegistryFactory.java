package io.virtue.registry;

import io.virtue.common.constant.Components;
import io.virtue.common.spi.ServiceInterface;
import io.virtue.common.url.ServiceFactory;

/**
 * Represents a registry factory for creating registry instances.
 */
@ServiceInterface(Components.Registry.CONSUL)
public interface RegistryFactory extends ServiceFactory<RegistryService> {

}


