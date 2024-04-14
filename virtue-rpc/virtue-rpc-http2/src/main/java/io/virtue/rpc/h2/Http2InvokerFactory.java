package io.virtue.rpc.h2;

import io.virtue.common.spi.Extension;
import io.virtue.core.*;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.HTTP2;

/**
 * Http2Invoker Factory.
 */
@Extension(HTTP2)
public class Http2InvokerFactory implements InvokerFactory {
    @Override
    public Callee<?> createCallee(Method method, RemoteService<?> remoteService) {
        return new Http2Callee(method, remoteService);
    }

    @Override
    public Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new Http2Caller(method, remoteCaller);
    }
}
