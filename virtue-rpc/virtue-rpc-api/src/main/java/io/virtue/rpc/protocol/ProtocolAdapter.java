package io.virtue.rpc.protocol;

import io.virtue.common.url.URL;
import io.virtue.core.*;

import java.lang.reflect.Method;

/**
 * InvokerFactory is used to create different caller for different protocols.
 */
public interface ProtocolAdapter {

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

    /**
     * Created when rpc call start.
     *
     * @param caller
     * @param args
     * @return
     */
    Invocation createInvocation(Caller<?> caller, Object[] args);

    /**
     * Created after the server deserializes.
     *
     * @param url
     * @param callee
     * @param args
     * @return
     */
    Invocation createInvocation(URL url, Callee<?> callee, Object[] args);

}
