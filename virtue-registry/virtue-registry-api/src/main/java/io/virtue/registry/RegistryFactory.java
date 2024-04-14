package io.virtue.registry;

import io.virtue.common.constant.Components;
import io.virtue.common.spi.Extensible;
import io.virtue.common.url.ServiceFactory;

/**
 * Represents a registry factory for creating registry instances.
 */
@Extensible(Components.Registry.CONSUL)
public interface RegistryFactory extends ServiceFactory<RegistryService> {

}


