package io.virtue.governance.loadbalance;

import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.config.Invocation;

import java.util.List;
import java.util.Random;

import static io.virtue.common.constant.Components.LoadBalance.ROUND_ROBIN;

/**
 * "随机"负载均衡策略:
 * 随机地将请求分配到服务器集群中的任意一台服务器
 * 这种策略简单快速，但可能会导致服务器负载不均衡
 */
@ServiceProvider(ROUND_ROBIN)
public class RandomLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected URL doChoose(Invocation invocation, List<URL> urls) {
        Random random = new Random();
        int index = random.nextInt(urls.size());
        return urls.get(index);
    }

}
