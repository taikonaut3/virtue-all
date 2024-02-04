package io.github.astro.virtue.governance.router;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.Invocation;

import java.util.List;

import static io.github.astro.virtue.common.constant.Components.Router.WEIGHT;

/**
 * Router interface.
 */
@ServiceInterface(WEIGHT)
public interface Router {

    /**
     * Routes the list of URLs based on the given call arguments.
     *
     * @param urls       the list of available URLs
     * @param invocation the invocation to be performed
     * @return the list of routed urls.
     */
    List<URL> route(List<URL> urls, Invocation invocation);
}

