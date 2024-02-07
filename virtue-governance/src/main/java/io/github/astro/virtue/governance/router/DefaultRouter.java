package io.github.astro.virtue.governance.router;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.Invocation;

import java.util.List;

import static io.github.astro.virtue.common.constant.Components.DEFAULT;

/**
 * DefaultRouter
 */
@ServiceProvider(DEFAULT)
public class DefaultRouter implements Router {

    @Override
    public List<URL> route(Invocation invocation, List<URL> urls) {
        return urls;
    }
}
