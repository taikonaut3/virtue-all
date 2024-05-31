package io.virtue.rpc.support;

import io.virtue.common.util.AssertUtil;
import io.virtue.core.Invoker;
import io.virtue.core.InvokerContainer;
import io.virtue.core.Virtue;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract CallerContainer.
 */
@Accessors(fluent = true)
public abstract class AbstractInvokerContainer implements InvokerContainer {

    protected Map<Method, Invoker<?>> invokers = new HashMap<>();

    @Getter
    protected Virtue virtue;

    @Setter
    protected String remoteApplication;

    @Getter
    protected String proxy;

    protected AbstractInvokerContainer(Virtue virtue) {
        AssertUtil.notNull(virtue);
        this.virtue = virtue;
    }

    @Override
    public void start() {
        for (Invoker<?> invoker : invokers.values()) {
            invoker.start();
        }
    }

    @Override
    public void stop() {
        for (Invoker<?> invoker : invokers.values()) {
            invoker.stop();
        }
        invokers.clear();
    }

    @Override
    public Invoker<?>[] invokers() {
        return invokers.values().toArray(Invoker[]::new);
    }

    @Override
    public Invoker<?> getInvoker(Method method) {
        return invokers.get(method);
    }

    @Override
    public String remoteApplication() {
        return remoteApplication;
    }

}
