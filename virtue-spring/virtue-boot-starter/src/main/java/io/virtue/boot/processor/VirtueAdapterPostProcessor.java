package io.virtue.boot.processor;

import io.virtue.core.Virtue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.NonNull;

/**
 * Get BeanFactory adapter.
 */
public abstract class VirtueAdapterPostProcessor implements BeanFactoryPostProcessor, BeanPostProcessor {

    protected ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    protected Virtue virtue() {
        if (beanFactory != null) {
            return beanFactory.getBean(Virtue.class);
        }
        return null;
    }
}
