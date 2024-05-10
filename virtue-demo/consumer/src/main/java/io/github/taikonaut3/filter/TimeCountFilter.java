package io.github.taikonaut3.filter;

import io.virtue.core.Invocation;
import io.virtue.core.MatchScope;
import io.virtue.core.Virtue;
import io.virtue.core.filter.Filter;
import io.virtue.core.manager.FilterManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static io.virtue.common.constant.Components.Protocol.*;

/**
 * @Author WenBo Zhou
 * @Date 2024/5/9 19:46
 */
@Component
public class TimeCountFilter implements Filter {

    public static final Wrapper h2Wrapper = new Wrapper();

    public static final Wrapper httpWrapper = new Wrapper();

    public static final Wrapper virtueWrapper = new Wrapper();

    public TimeCountFilter(Virtue virtue) {
        FilterManager manager = virtue.configManager().filterManager();
        manager.addProtocolRule(this, MatchScope.INVOKER, ".*");
    }

    @Override
    public Object doFilter(Invocation invocation) {
        long start = System.currentTimeMillis();
        try {
            return invocation.invoke();
        } finally {
            long end = System.currentTimeMillis();
            long t = end - start;
            String protocol = invocation.url().protocol();
            System.out.println(protocol + "耗时:" + t);
            switch (protocol) {
                case H2 -> {
                    h2Wrapper.totalTime.addAndGet(t);
                    h2Wrapper.count.incrementAndGet();
                }
                case HTTPS -> {
                    httpWrapper.totalTime.addAndGet(t);
                    httpWrapper.count.incrementAndGet();
                }
                case VIRTUE -> {
                    virtueWrapper.totalTime.addAndGet(t);
                    virtueWrapper.count.incrementAndGet();
                }
            }
        }
    }

    public static class Wrapper {
        public AtomicLong totalTime = new AtomicLong(0);

        public AtomicInteger count = new AtomicInteger(0);
    }
}
