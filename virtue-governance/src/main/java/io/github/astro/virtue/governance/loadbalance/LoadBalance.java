package io.github.astro.virtue.governance.loadbalance;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.Invocation;

import java.util.List;

import static io.github.astro.virtue.common.constant.Components.LoadBalance.RANDOM;

/**
 * Load balancing interface.
 */
@ServiceInterface(RANDOM)
public interface LoadBalance {

    /**
     * Selects an url for the given invocation from a list of available urls.
     *
     * @param invocation the invocation to be performed
     * @param urls       the list of available URLs
     * @return the selected url
     */
    URL select(Invocation invocation, List<URL> urls);
}

