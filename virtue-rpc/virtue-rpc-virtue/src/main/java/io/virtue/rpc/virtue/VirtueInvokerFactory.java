package io.virtue.rpc.virtue;

import io.virtue.common.url.URL;
import io.virtue.core.*;

import java.lang.reflect.Method;

/**
 * Virtue InvokerFactory.
 */
public class VirtueInvokerFactory implements InvokerFactory {

    @Override
    public Callee<?> createCallee(Method method, RemoteService<?> remoteService) {
        return new VirtueCallee(method, remoteService);
    }

    @Override
    public Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new VirtueCaller(method, remoteCaller);
    }

    @Override
    public Invocation createInvocation(Caller<?> caller, Object[] args) {
        return new VirtueInvocation(caller, args);
    }

    @Override
    public Invocation createInvocation(URL url, Callee<?> callee, Object[] args) {
        return new VirtueInvocation(url, callee, args);
    }
}
