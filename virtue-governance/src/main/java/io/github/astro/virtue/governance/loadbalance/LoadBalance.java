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
     * Selects a URL for the given invocation from a list of available URLs.
     *
     * @param invocation The invocation to be performed.
     * @param urls       The list of available URLs.
     * @return The selected URL.
     */
    URL select(Invocation invocation, List<URL> urls);
}

