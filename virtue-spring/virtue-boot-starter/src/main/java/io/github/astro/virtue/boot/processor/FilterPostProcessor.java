package io.github.astro.virtue.boot.processor;

import io.github.astro.virtue.config.Virtue;
import io.github.astro.virtue.config.filter.Filter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class FilterPostProcessor implements BeanFactoryPostProcessor, BeanPostProcessor {

    private BeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Filter filter) {
            Virtue virtue = beanFactory.getBean(Virtue.class);
            virtue.registerFilter(beanName, filter);
        }
        return bean;
    }

}
