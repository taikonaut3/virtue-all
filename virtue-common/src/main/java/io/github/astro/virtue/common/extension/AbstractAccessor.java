package io.github.astro.virtue.common.extension;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAccessor<V> implements Accessor<V> {

    protected Map<String, V> params = new HashMap<>();

    @Override
    public void setData(Map<String, V> params) {
        this.params.putAll(params);
    }

    @Override
    public void replaceAccessor(Map<String, V> params) {
        this.params = params;
    }

    @Override
    public void setData(String key, V value) {
        params.put(key, value);
    }

    @Override
    public V getData(String key) {
        return params.get(key);
    }

    @Override
    public V getData(String key, V defaultValue) {
        return params.getOrDefault(key, defaultValue);
    }

    @Override
    public V getDataOrPut(String key, V value) {
        V v = params.get(key);
        if (v == null) {
            params.put(key, value);
            v = value;
        }
        return v;

    }

    @Override
    public Map<String, V> getAccessor() {
        return params;
    }
}
