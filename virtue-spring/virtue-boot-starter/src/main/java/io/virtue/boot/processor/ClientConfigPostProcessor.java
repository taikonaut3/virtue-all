package io.virtue.boot.processor;

import io.virtue.core.config.ClientConfig;
import org.springframework.beans.BeansException;
import org.springframework.lang.NonNull;

/**
 * ClientConfig PostProcessor.
 */
public class ClientConfigPostProcessor extends VirtueAdapterPostProcessor {
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof ClientConfig config) {
            config.name(beanName);
            virtue().register(config);
        }
        return bean;
    }
}
