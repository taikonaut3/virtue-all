package io.virtue.governance.router;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.spi.ServiceInterface;
import io.virtue.common.url.URL;
import io.virtue.config.Invocation;

import java.util.List;

import static io.virtue.common.constant.Components.DEFAULT;

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

