package io.github.astro.virtue.rpc;

import io.github.astro.virtue.config.Caller;
import io.github.astro.virtue.config.RemoteCaller;
import io.github.astro.virtue.config.RpcCallArgs;
import io.github.astro.virtue.proxy.InvocationHandler;
import io.github.astro.virtue.proxy.SuperInvoker;

import java.lang.reflect.Method;

public class ClientInvocationHandler implements InvocationHandler {

    private final RemoteCaller<?> remoteCaller;

    public ClientInvocationHandler(RemoteCaller<?> remoteCaller) {
        this.remoteCaller = remoteCaller;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args, SuperInvoker<?> superInvoker) throws Throwable {
        Caller<?> caller = remoteCaller.getCaller(method);
        if (caller != null) {
            RpcCallArgs data = new RpcCallArgs(caller, args);
            return caller.call(data);
        }
        return null;
    }

}
