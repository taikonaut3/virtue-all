package io.virtue.governance.loadbalance;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.common.util.AtomicUtil;
import io.virtue.core.Invocation;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.virtue.common.constant.Components.LoadBalance.ROUND_ROBIN;

/**
 * "RoundRobin" load balancing strategy:
 * Requests are assigned to each server in sequence,
 * Each request is assigned in the order of the server list, and starts again at the end of the list,
 * This strategy applies to cases where the server performance is equivalent.
 */
@Extension(ROUND_ROBIN)
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected URL doChoose(Invocation invocation, List<URL> urls) {
        AtomicInteger lastIndex = invocation.invoker().get(Key.LAST_CALL_INDEX);
        int current = AtomicUtil.updateAtomicInteger(lastIndex, old -> (old + 1) % urls.size());
        return urls.get(current);
    }

}
