package io.virtue.common.extension;

import io.virtue.common.extension.resoruce.Cleanable;

import java.util.HashMap;
import java.util.Map;

/**
 * Store data for type String.
 *
 * @param <T> current instance
 */
@SuppressWarnings("unchecked")
public abstract class StringAccessor<T> implements Cleanable {

    protected Map<String, String> accessor = new HashMap<>();

    /**
     * Set data.
     *
     * @param key
     * @param value
     * @return
     */
    public T set(String key, String value) {
        accessor.put(key, value);
        return (T) this;
    }

    /**
     * Remove data.
     *
     * @param key
     * @return
     */
    public T remove(String key) {
        accessor.remove(key);
        return (T) this;
    }

    /**
     * Get data.
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return accessor.get(key);
    }

    @Override
    public void clear() {
        accessor.clear();
    }
}
