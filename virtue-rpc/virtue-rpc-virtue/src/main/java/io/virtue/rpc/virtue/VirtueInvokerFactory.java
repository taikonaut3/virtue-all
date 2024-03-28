package io.virtue.rpc.virtue;

import io.virtue.common.constant.Components;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.core.*;

import java.lang.reflect.Method;

/**
 * Virtue InvokerFactory.
 */
@ServiceProvider(Components.Protocol.VIRTUE)
public class VirtueInvokerFactory implements InvokerFactory {

    @Override
    public Callee<?> createCallee(Method method, RemoteService<?> remoteService) {
        return new VirtueCallee(method, remoteService);
    }

    @Override
    public Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new VirtueCaller(method, remoteCaller);
    }
}
