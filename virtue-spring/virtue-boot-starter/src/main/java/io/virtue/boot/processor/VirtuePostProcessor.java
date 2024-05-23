package io.virtue.boot.processor;

import io.virtue.boot.EnvironmentKey;
import io.virtue.common.exception.ResourceException;
import io.virtue.core.Virtue;
import org.springframework.beans.BeansException;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Virtue PostProcessor.
 */
public class VirtuePostProcessor extends VirtueAdapterPostProcessor {

    private ConfigurableEnvironment environment;

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof Virtue virtue) {
            environment = beanFactory.getBean(ConfigurableEnvironment.class);
            String applicationName = environment.getProperty(EnvironmentKey.APPLICATION_NAME);
            if (!StringUtils.hasText(applicationName)) {
                applicationName = environment.getProperty(EnvironmentKey.SPRING_APPLICATION_NAME);
                if (!StringUtils.hasText(applicationName)) {
                    throw new ResourceException("缺少application-name");
                }
            }
            virtue.applicationName(applicationName);
            for (String key : getPropertyKeys()) {
                virtue.environment().set(key, environment.getProperty(key));
            }
        }
        return bean;
    }

    public Set<String> getPropertyKeys() {
        Set<String> keys = new LinkedHashSet<>();
        MutablePropertySources propertySources = environment.getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (propertySource instanceof EnumerablePropertySource<?> enumerablePropertySource && !propertySource.getName().startsWith("system")) {
                Collections.addAll(keys, enumerablePropertySource.getPropertyNames());
            }
        }
        return keys;
    }

}
