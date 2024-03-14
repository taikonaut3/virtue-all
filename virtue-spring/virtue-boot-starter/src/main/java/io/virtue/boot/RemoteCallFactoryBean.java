package io.virtue.boot;

import io.virtue.config.RemoteCaller;
import io.virtue.config.manager.Virtue;
import org.springframework.beans.factory.FactoryBean;

public class RemoteCallFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> interfaceType;

    private Virtue virtue;

    private RemoteCaller<T> remoteCaller;

    public RemoteCallFactoryBean(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

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