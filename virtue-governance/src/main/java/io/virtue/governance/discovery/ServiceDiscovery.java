package io.virtue.governance.discovery;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.spi.Extensible;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;

import java.util.List;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * ServiceDiscovery for discover available services from the registry.
 */
@Extensible(value = DEFAULT, key = Key.SERVICE_DISCOVERY)
public interface ServiceDiscovery {

    /**
     * discover all available services from the registry.
     *
     * @param invocation      the invocation to be used for the service lookup
     * @param registryConfigs the registry configurations to be used for the service lookup
     * @return a list of urls representing the available services
     */
    List<URL> discover(Invocation invocation, URL... registryConfigs);

}

