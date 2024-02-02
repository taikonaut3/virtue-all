package io.github.astro.virtue.boot.processor;

import io.github.astro.virtue.config.config.ServerConfig;
import org.springframework.beans.BeansException;
import org.springframework.lang.NonNull;

public class ServerConfigPostProcessor extends VirtueAdapterPostProcessor {


    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof ServerConfig config) {
            config.name(beanName);
            virtue().register(config);
        }
        return bean;
    }

}
