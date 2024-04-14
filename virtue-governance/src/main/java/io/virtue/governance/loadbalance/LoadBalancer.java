package io.virtue.governance.loadbalance;

import io.virtue.common.spi.Extensible;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;

import java.util.List;

import static io.virtue.common.constant.Components.LoadBalance.RANDOM;

/**
 * Load balancing interface.
 */
@Extensible(RANDOM)
public interface LoadBalancer {

    /**
     * Choose an url for the given invocation from a list of available urls.
     *
     * @param invocation the invocation to be performed
     * @param urls       the list of available URLs
     * @return the selected url
     */
    URL choose(Invocation invocation, List<URL> urls);
}

