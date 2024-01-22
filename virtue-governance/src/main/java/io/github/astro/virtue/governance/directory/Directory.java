package io.github.astro.virtue.governance.directory;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.Invocation;

import java.util.List;

import static io.github.astro.virtue.common.constant.Components.Directory.DEFAULT;

/**
 * Directory for listing available services from the registry.
 */
@ServiceInterface(DEFAULT)
public interface Directory {

    /**
     * Lists all available services from the registry.
     *
     * @param invocation      The invocation to be used for the service lookup.
     * @param registryConfigs The registry configurations to be used for the service lookup.
     * @return A list of URLs representing the available services.
     */
    List<URL> list(Invocation invocation, URL... registryConfigs);

    /**
     * Destroys the cache used by the directory.
     */
    void destroy();
}

