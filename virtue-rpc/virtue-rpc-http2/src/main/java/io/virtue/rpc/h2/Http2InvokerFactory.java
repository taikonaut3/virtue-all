package io.virtue.rpc.h2;

import io.virtue.common.url.URL;
import io.virtue.core.*;

import java.lang.reflect.Method;

/**
 * Http2Invoker Factory.
 */
public class Http2InvokerFactory implements InvokerFactory {

    @Override
    public Callee<?> createCallee(Method method, RemoteService<?> remoteService) {
        return new Http2Callee(method, remoteService);
    }

    @Override
    public Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new Http2Caller(method, remoteCaller);
    }

    @Override
    public Invocation createInvocation(Caller<?> caller, Object[] args) {
        return new Http2Invocation(caller, args);
    }

    @Override
    public Invocation createInvocation(URL url, Callee<?> callee, Object[] args) {
        return new Http2Invocation(url, callee, args);
    }
}
