package io.github.taikonaut3.filter;

import io.virtue.core.Invocation;
import io.virtue.core.filter.Filter;
import org.example.Result;
import org.springframework.stereotype.Component;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/26 15:44
 */
@Component
public class CallerResultFilter implements Filter {
    @Override
    public Object doFilter(Invocation invocation) {
        Object data = invocation.invoke();
        if (data instanceof Result result) {
            return result.getData();
        }
        return data;
    }
}
