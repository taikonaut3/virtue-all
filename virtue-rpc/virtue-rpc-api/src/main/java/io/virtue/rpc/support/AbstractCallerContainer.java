package io.virtue.rpc.support;

import io.virtue.common.util.AssertUtil;
import io.virtue.config.Caller;
import io.virtue.config.CallerContainer;
import io.virtue.config.manager.Virtue;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Accessors(fluent = true)
public abstract class AbstractCallerContainer implements CallerContainer {

    protected Map<Method, Caller<?>> callerMap = new HashMap<>();
    protected Map<String, Caller<?>> identificationCallerMap = new HashMap<>();
    @Getter
    protected Virtue virtue;
    @Setter
    protected String remoteApplication;
    @Getter
    protected String proxy;

    protected AbstractCallerContainer(Virtue virtue) {
        AssertUtil.notNull(virtue);
        this.virtue = virtue;
    }

    @Override
    public void start() {
        for (Caller<?> caller : callerMap.values()) {
            caller.start();
        }
    }

    @Override
    public void stop() {
        callerMap.clear();
        identificationCallerMap.clear();
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

    @Override
    public Caller<?> getCaller(String identification) {
        return identificationCallerMap.get(identification);
    }
}
