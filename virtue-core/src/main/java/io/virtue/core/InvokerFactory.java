package io.virtue.core;

import io.virtue.common.spi.Extensible;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * InvokerFactory is used to create different caller for different protocols.
 */
@Extensible(VIRTUE)
public interface InvokerFactory {

    /**
     * Create serverCaller.
     *
     * @param method
     * @param remoteService
     * @return serverCaller instance
     */
    Callee<?> createCallee(Method method, RemoteService<?> remoteService);

    /**
     * Create clientCaller.
     *
     * @param method
     * @param remoteCaller
     * @return clientCaller instance
     */
    Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller);

}
