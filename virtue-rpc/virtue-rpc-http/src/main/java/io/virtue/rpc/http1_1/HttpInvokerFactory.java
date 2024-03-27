package io.virtue.rpc.http1_1;

import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.constant.Components;
import io.virtue.core.*;

import java.lang.reflect.Method;

@ServiceProvider(Components.Protocol.HTTP)
public class HttpInvokerFactory implements InvokerFactory {
    @Override
    public Callee<?> createCallee(Method method, RemoteService<?> remoteService) {
        return new HttpCallee(method, remoteService);
    }

    @Override
    public Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new HttpCaller(method, remoteCaller);
    }
}
