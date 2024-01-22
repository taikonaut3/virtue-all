package io.github.astro.virtue.config.manager;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractManager<T> {

    protected Map<String, T> map;

    public AbstractManager() {
        map = new HashMap<>();
    }

    public void register(String name, T value) {
        map.put(name, value);
    }

    public void remove(String name) {
        map.remove(name);
    }

    public T get(String name) {
        return map.get(name);
    }

    public Map<String, T> getManagerMap() {
        return map;
    }

}
