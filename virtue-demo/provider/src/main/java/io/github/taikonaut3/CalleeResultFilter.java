package io.github.taikonaut3;

import io.virtue.core.Invocation;
import io.virtue.core.filter.Filter;
import org.example.Result;
import org.springframework.stereotype.Component;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/26 15:41
 */
@Component
public class CalleeResultFilter implements Filter {
    @Override
    public Object doFilter(Invocation invocation) {
        Object data = invocation.invoke();
        return new Result("0000", data, "成功");
    }
}
