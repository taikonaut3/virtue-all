package io.virtue.governance.loadbalance;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.config.Invocation;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.virtue.common.constant.Components.LoadBalance.ROUND_ROBIN;

/**
 * The "Polling" load-balancing policy:
 * Requests are assigned to each server in sequence,
 * Each request is assigned in the order of the server list, and starts again at the end of the list,
 * This strategy applies to cases where the server performance is equivalent.
 */
@ServiceProvider(ROUND_ROBIN)
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected URL doChoose(Invocation invocation, List<URL> urls) {
        URL url = invocation.callArgs().caller().url();
        AtomicInteger lastIndex = url.attribute(Key.LAST_CALL_INDEX_ATTRIBUTE_KEY).get();
        int current;
        do {
            current = (lastIndex.get() + 1) % urls.size();
        } while (!lastIndex.compareAndSet(lastIndex.get(), current));
        return urls.get(current);
    }

}
