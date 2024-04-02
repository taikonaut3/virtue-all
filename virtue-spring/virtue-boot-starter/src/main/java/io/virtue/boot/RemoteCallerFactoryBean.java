package io.virtue.boot;

import io.virtue.core.RemoteCaller;
import io.virtue.core.Virtue;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * RemoteCaller FactoryBean.
 * @param <T>
 */
public class RemoteCallerFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> interfaceType;

    private BeanFactory beanFactory;

    private RemoteCaller<T> remoteCaller;

    public RemoteCallerFactoryBean(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Resource
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        if (remoteCaller == null) {
            Virtue virtue = beanFactory.getBean(Virtue.class);
            remoteCaller = virtue.proxy(interfaceType).remoteCaller(interfaceType);
            var annotation = interfaceType.getAnnotation(io.virtue.core.annotation.RemoteCaller.class);
            if (interfaceType.isAssignableFrom(annotation.fallback())) {
                remoteCaller.fallBacker((T) beanFactory.getBean(annotation.fallback()));
            }
        }
        return remoteCaller.get();
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

}
