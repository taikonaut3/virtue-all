package io.virtue.rpc.virtue;

import io.virtue.common.spi.Extension;
import io.virtue.core.*;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * Virtue InvokerFactory.
 */
@Extension(VIRTUE)
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
