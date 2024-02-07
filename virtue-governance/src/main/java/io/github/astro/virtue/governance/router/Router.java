package io.github.astro.virtue.governance.router;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.extension.AttributeKey;
import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.Invocation;

import java.util.List;

import static io.github.astro.virtue.common.constant.Components.DEFAULT;

/**
 * Router interface.
 */
@ServiceInterface(DEFAULT)
public interface Router {

    AttributeKey<Router> ATTRIBUTE_KEY = AttributeKey.get(Key.ROUTER);

    /**
     * Routes the list of URLs based on the given call arguments.
     *
     * @param invocation the invocation to be performed
     * @return the list of routed urls.
     */
    List<URL> route(Invocation invocation, List<URL> urls);
}

