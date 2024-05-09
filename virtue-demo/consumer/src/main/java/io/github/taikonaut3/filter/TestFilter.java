package io.github.taikonaut3.filter;

import io.virtue.common.extension.RpcContext;
import io.virtue.core.Invocation;
import io.virtue.core.MatchScope;
import io.virtue.core.Virtue;
import io.virtue.core.filter.Filter;
import io.virtue.core.manager.FilterManager;
import org.springframework.stereotype.Component;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/15 16:24
 */
@Component
public class TestFilter implements Filter {

    public TestFilter(Virtue virtue) {
        FilterManager manager = virtue.configManager().filterManager();
        manager.addProtocolRule(this, MatchScope.CALLER, "h2");
    }
    @Override
    public Object doFilter(Invocation invocation) {
        try {
            RpcContext.requestContext().set("qqq", "www");
            return invocation.invoke();
        } finally {
            String s = RpcContext.responseContext().get("123");
//            System.out.println(s);
        }
    }
}
