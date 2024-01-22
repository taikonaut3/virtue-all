package io.github.astro.virtue.common.extension;

import java.util.HashMap;
import java.util.Map;

public class RpcContext {

    private static final ThreadLocal<RpcContext> threadLocal = new ThreadLocal<>();

    private Map<String, Object> contextMap;

    private RpcContext() {
        contextMap = new HashMap<>();
    }

    public static RpcContext getContext() {
        RpcContext context = threadLocal.get();
        if (context == null) {
            context = new RpcContext();
            threadLocal.set(context);
        }
        return context;
    }

    public RpcContext set(String key, Object value) {
        contextMap.put(key, value);
        return this;
    }

    public Object get(String key) {
        return contextMap.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = contextMap.get(key);
        if (value != null) {
            return type.cast(value);
        }
        return null;
    }

    public RpcContext remove(String key) {
        contextMap.remove(key);
        return this;
    }

    public void clear() {
        contextMap.clear();
    }

    public Map<String, Object> getAll() {
        return new HashMap<>(contextMap);
    }

}
