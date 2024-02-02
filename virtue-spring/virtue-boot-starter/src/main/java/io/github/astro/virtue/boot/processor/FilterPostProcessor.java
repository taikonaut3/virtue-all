package io.github.astro.virtue.boot.processor;

import io.github.astro.virtue.config.filter.Filter;
import org.springframework.beans.BeansException;
import org.springframework.lang.NonNull;

public class FilterPostProcessor extends VirtueAdapterPostProcessor {

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof Filter filter) {
            virtue().register(beanName, filter);
        }
        return bean;
    }

}
