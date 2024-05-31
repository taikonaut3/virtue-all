package io.github.taikonaut3.filter;

import io.virtue.core.Invocation;
import io.virtue.core.MatchScope;
import io.virtue.core.Virtue;
import io.virtue.core.filter.Filter;
import io.virtue.core.manager.FilterManager;
import io.virtue.metrics.CallerMetrics;
import org.springframework.stereotype.Component;

import static io.virtue.common.constant.Components.Protocol.*;

/**
 * @Author WenBo Zhou
 * @Date 2024/5/9 19:46
 */
@Component
public class CallerMetricsExportFilter implements Filter {

    public static CallerMetrics h2Wrapper;

    public static CallerMetrics httpWrapper;

    public static CallerMetrics virtueWrapper;

    public CallerMetricsExportFilter(Virtue virtue) {
        FilterManager manager = virtue.configManager().filterManager();
        manager.addProtocolRule(this, MatchScope.INVOKER, ".*");
    }

    @Override
    public Object doFilter(Invocation invocation) {
        String protocol = invocation.url().protocol();
        CallerMetrics callerMetrics = invocation.invoker().get(CallerMetrics.ATTRIBUTE_KEY);
        switch (protocol) {
            case H2 -> h2Wrapper = callerMetrics;
            case HTTPS -> httpWrapper = callerMetrics;
            case VIRTUE -> virtueWrapper = callerMetrics;
        }
        return invocation.invoke();
    }
}
