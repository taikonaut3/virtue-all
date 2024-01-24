package io.github.astro.virtue.boot;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;

public class ServiceRegistrationPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractAutoServiceRegistration<?> autoServiceRegistration) {
            autoServiceRegistration.addRegistrationLifecycle(new VirtueRegistrationLifecycle<>());
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

}
