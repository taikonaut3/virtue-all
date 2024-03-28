package io.virtue.boot;

import io.virtue.core.RemoteCaller;
import io.virtue.core.Virtue;
import org.springframework.beans.factory.FactoryBean;

/**
 * RemoteCaller FactoryBean.
 * @param <T>
 */
public class RemoteCallerFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> interfaceType;

    private Virtue virtue;

    private RemoteCaller<T> remoteCaller;

    public RemoteCallerFactoryBean(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
     * Set virtue.
     * @param virtue
     */
    public void setVirtue(Virtue virtue) {
        this.virtue = virtue;
    }

    @Override
    public T getObject() throws Exception {
        if (remoteCaller == null) {
            remoteCaller = virtue.proxy(interfaceType).remoteCaller(interfaceType);
        }
        return remoteCaller.get();
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

}
