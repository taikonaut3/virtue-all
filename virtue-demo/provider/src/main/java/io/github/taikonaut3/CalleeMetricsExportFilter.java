package io.github.taikonaut3;

import io.virtue.core.Invocation;
import io.virtue.core.MatchScope;
import io.virtue.core.Virtue;
import io.virtue.core.filter.Filter;
import io.virtue.core.manager.FilterManager;
import io.virtue.metrics.CalleeMetrics;
import org.springframework.stereotype.Component;

import static io.virtue.common.constant.Components.Protocol.*;

/**
 * @Author WenBo Zhou
 * @Date 2024/5/9 19:46
 */
@Component
public class CalleeMetricsExportFilter implements Filter {

    public static CalleeMetrics h2Wrapper;

    public static CalleeMetrics httpWrapper;

    public static CalleeMetrics virtueWrapper;

    public CalleeMetricsExportFilter(Virtue virtue) {
        FilterManager manager = virtue.configManager().filterManager();
        manager.addProtocolRule(this, MatchScope.INVOKER, ".*");
    }

    @Override
    public Object doFilter(Invocation invocation) {
        String protocol = invocation.url().protocol();
        CalleeMetrics calleeMetrics = invocation.invoker().get(CalleeMetrics.ATTRIBUTE_KEY);
        switch (protocol) {
            case H2 -> h2Wrapper = calleeMetrics;
            case HTTPS -> httpWrapper = calleeMetrics;
            case VIRTUE -> virtueWrapper = calleeMetrics;
        }
        return invocation.invoke();
    }
}
