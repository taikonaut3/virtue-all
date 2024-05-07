package io.virtue.registry;

import io.virtue.common.extension.spi.Extensible;
import io.virtue.common.url.ServiceFactory;

import static io.virtue.common.constant.Components.Registry.CONSUL;

/**
 * Represents a registry factory for creating registry instances.
 */
@Extensible(CONSUL)
public interface RegistryFactory extends ServiceFactory<RegistryService> {

}


