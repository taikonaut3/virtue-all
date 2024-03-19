package io.virtue.governance.loadbalance;

import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.virtue.common.constant.Components.LoadBalance.ROUND_ROBIN;

/**
 * "Random" load balancing strategy:
 * Randomly assign requests to any server in the server cluster,
 * This strategy is simple and fast, but it can lead to an uneven server load.
 */
@ServiceProvider(ROUND_ROBIN)
public class RandomLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected URL doChoose(Invocation invocation, List<URL> urls) {
        int index = ThreadLocalRandom.current().nextInt(urls.size());
        return urls.get(index);
    }

}
