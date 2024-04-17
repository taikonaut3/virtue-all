package io.virtue.core;

import java.lang.reflect.Method;

/**
 * InvokerFactory is used to create different caller for different protocols.
 */
public interface InvokerFactory extends InvocationFactory{

    /**
     * Create server Callee.
     *
     * @param method
     * @param remoteService
     * @return serverCallee instance
     */
    Callee<?> createCallee(Method method, RemoteService<?> remoteService);

    /**
     * Create client Caller.
     *
     * @param method
     * @param remoteCaller
     * @return clientCaller instance
     */
    Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller);

}
