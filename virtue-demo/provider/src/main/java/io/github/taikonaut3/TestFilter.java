package io.github.taikonaut3;

import io.virtue.common.extension.RpcContext;
import io.virtue.core.Invocation;
import io.virtue.core.filter.Filter;
import org.springframework.stereotype.Component;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/15 16:15
 */
@Component
public class TestFilter implements Filter {
    @Override
    public Object doFilter(Invocation invocation) {
        try {
            return invocation.invoke();
        } finally {
            RpcContext.responseContext().set("123", "456");
        }
    }
}
