package io.github.astro.model.filter;

import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.config.filter.Filter;
import org.springframework.stereotype.Component;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/12 20:09
 */
@Component
public class Filter2 implements Filter {

    @Override
    public Object doFilter(Invocation invocation) {
        System.out.println("Filter222222");
        System.out.println(invocation.callArgs().caller().url());
        return invocation.invoke();
    }
}
