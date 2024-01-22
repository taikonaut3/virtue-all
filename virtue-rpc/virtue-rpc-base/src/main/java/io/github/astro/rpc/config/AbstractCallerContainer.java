package io.github.astro.rpc.config;

import io.github.astro.virtue.config.Caller;
import io.github.astro.virtue.config.CallerContainer;
import io.github.astro.virtue.config.Virtue;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Accessors(fluent = true)
public abstract class AbstractCallerContainer implements CallerContainer {

    protected Map<Method, Caller<?>> callerMap = new HashMap<>();

    protected Virtue virtue;

    @Setter
    protected String remoteApplication;

    @Getter
    protected String proxy;

    protected AbstractCallerContainer() {
        virtue = Virtue.getDefault();
    }

    @Override
    public void start() {
        for (Caller<?> caller : callers()) {
            caller.start();
        }
    }

    @Override
    public Caller<?>[] callers() {
        return callerMap.values().toArray(Caller[]::new);
    }

    @Override
    public Caller<?> getCaller(Method method) {
        return callerMap.get(method);
    }

    @Override
    public String remoteApplication() {
        return remoteApplication;
    }
}
