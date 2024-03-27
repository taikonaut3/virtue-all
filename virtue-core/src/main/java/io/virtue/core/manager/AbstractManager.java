package io.virtue.core.manager;

import io.virtue.core.Virtue;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Manager.
 * @param <T> manage type
 */
public abstract class AbstractManager<T> {

    protected final Virtue virtue;

    protected Map<String, T> map;

    protected AbstractManager(Virtue virtue) {
        this.virtue = virtue;
        this.map = new HashMap<>();
    }

    /**
     * Add config.
     * @param name
     * @param value
     */
    public void register(String name, T value) {
        map.put(name, value);
    }

    /**
     * Remove config by name.
     * @param name
     */
    public void remove(String name) {
        map.remove(name);
    }

    /**
     * Get config by name.
     * @param name
     * @return
     */
    public T get(String name) {
        return map.get(name);
    }

    /**
     * Get all config map.
     * @return
     */
    public Map<String, T> getManagerMap() {
        return map;
    }

}
