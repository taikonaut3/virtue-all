package io.virtue.governance.loadbalance;

import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.common.util.CollectionUtil;
import io.virtue.core.Invocation;

import java.util.List;

/**
 * Abstract LoadBalancer.
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {

    @Override
    public URL choose(Invocation invocation, List<URL> urls) {
        if (CollectionUtil.isEmpty(urls)) {
            throw new RpcException("Not available Service Urls");
        }
        if (urls.size() == 1) {
            return urls.get(0);
        }
        return doChoose(invocation, urls);
    }

    protected abstract URL doChoose(Invocation invocation, List<URL> urls);

}
