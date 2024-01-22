package io.github.astro.virtue.boot;

import io.github.astro.rpc.ComplexRemoteCaller;
import io.github.astro.virtue.config.RemoteCaller;
import org.springframework.beans.factory.FactoryBean;

public class RemoteCallFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> interfaceType;

    private final RemoteCaller<T> remoteCaller;

    public RemoteCallFactoryBean(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
        remoteCaller = new ComplexRemoteCaller<>(interfaceType);
    }

    @Override
    public T getObject() throws Exception {
        return remoteCaller.get();
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

}
