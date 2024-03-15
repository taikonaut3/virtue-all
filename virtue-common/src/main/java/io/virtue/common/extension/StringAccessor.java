package io.virtue.common.extension;

import java.util.HashMap;
import java.util.Map;

/**
 * Store data for type String
 */
@SuppressWarnings("unchecked")
public abstract class StringAccessor<T> {

    protected Map<String, String> accessor = new HashMap<>();

    public T set(String key, String value) {
        accessor.put(key, value);
        return (T) this;
    }

    public T remove(String key) {
        accessor.remove(key);
        return (T) this;
    }

    public String get(String key) {
        return accessor.get(key);
    }
}
