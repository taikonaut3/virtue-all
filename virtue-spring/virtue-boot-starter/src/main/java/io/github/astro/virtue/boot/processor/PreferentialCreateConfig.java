package io.github.astro.virtue.boot.processor;

import io.github.astro.virtue.boot.RemoteCallFactoryBean;
import io.github.astro.virtue.config.annotation.RemoteService;
import io.github.astro.virtue.config.config.ClientConfig;
import io.github.astro.virtue.config.config.RegistryConfig;
import io.github.astro.virtue.config.config.ServerConfig;
import io.github.astro.virtue.config.filter.Filter;
import org.springframework.beans.BeansException;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Preferential Create These Bean
 */
public abstract class PreferentialCreateConfig extends VirtueAdapterPostProcessor {

    private final static List<Class<?>> PREFERENTIAL_BEAN_TYPES = List.of(
            ServerConfig.class, ClientConfig.class, RegistryConfig.class, Filter.class
    );

    private static boolean isCreate = false;

    @Override
    public Object postProcessBeforeInitialization(Object bean, @NonNull String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RemoteService.class) || bean instanceof RemoteCallFactoryBean<?>) {
            createConfigBean();
        }
        return bean;
    }

    protected void createConfigBean() {
        if (!isCreate) {
            for (Class<?> configType : PREFERENTIAL_BEAN_TYPES) {
                beanFactory.getBeansOfType(configType);
            }
            isCreate = true;
        }
    }
}
