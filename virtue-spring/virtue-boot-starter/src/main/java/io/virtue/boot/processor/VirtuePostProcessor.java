package io.virtue.boot.processor;

import io.virtue.boot.EnvironmentKey;
import io.virtue.common.exception.SourceException;
import io.virtue.core.manager.Virtue;
import org.springframework.beans.BeansException;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

public class VirtuePostProcessor extends VirtueAdapterPostProcessor {

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof Virtue virtue) {
            Environment environment = beanFactory.getBean(Environment.class);
            String applicationName = environment.getProperty(EnvironmentKey.APPLICATION_NAME);
            if (!StringUtils.hasText(applicationName)) {
                applicationName = environment.getProperty(EnvironmentKey.SPRING_APPLICATION_NAME);
                if (!StringUtils.hasText(applicationName)) {
                    throw new SourceException("缺少application-name");
                }
            }
            virtue.applicationName(applicationName);
        }
        return bean;
    }

}
