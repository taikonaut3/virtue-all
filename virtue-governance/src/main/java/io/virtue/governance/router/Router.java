package io.virtue.governance.router;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.extension.spi.Extensible;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;

import java.util.List;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * Router interface.
 */
@Extensible(DEFAULT)
public interface Router {

    AttributeKey<Router> ATTRIBUTE_KEY = AttributeKey.of(Key.ROUTER);

    /**
     * Routes the list of URLs based on the given invocation and server urls.
     *
     * @param invocation
     * @param urls
     * @return
     */
    List<URL> route(Invocation invocation, List<URL> urls);
}

