package io.virtue.governance.directory;

import io.virtue.common.spi.ServiceInterface;
import io.virtue.common.url.URL;
import io.virtue.config.Invocation;

import java.util.List;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * Directory for listing available services from the registry.
 */
@ServiceInterface(DEFAULT)
public interface Directory {

    /**
     * Lists all available services from the registry.
     *
     * @param invocation      the invocation to be used for the service lookup
     * @param registryConfigs the registry configurations to be used for the service lookup
     * @return a list of urls representing the available services
     */
    List<URL> list(Invocation invocation, URL... registryConfigs);

    /**
     * Destroys the cache used by the directory.
     */
    void destroy();
}

