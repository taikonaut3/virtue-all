package io.github.taikonaut3.filter;

import io.virtue.common.extension.RpcContext;
import io.virtue.core.Invocation;
import io.virtue.core.filter.Filter;
import org.springframework.stereotype.Component;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/15 16:24
 */
@Component
public class TestFilter implements Filter {
    @Override
    public Object doFilter(Invocation invocation) {
        try {
            RpcContext.requestContext().set("qqq","www");
            return invocation.invoke();
        } finally {
            String s = RpcContext.responseContext().get("123");
            System.out.println(s);
        }
    }
}
