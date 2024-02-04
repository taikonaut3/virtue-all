package io.github.astro.filter;

import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.config.filter.Filter;
import org.springframework.stereotype.Component;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/12 20:09
 */
@Component
public class Filter1 implements Filter {

    @Override
    public Object doFilter(Invocation invocation) {
        URL url = invocation.url();
        System.out.println("filter1111111");
        System.out.println(url);
        return invocation.invoke();
    }
}
