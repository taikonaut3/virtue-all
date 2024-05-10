package io.virtue.common.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Accessor abstract class.
 */

@SuppressWarnings("unchecked")
public abstract class AbstractAccessor implements Accessor {

    protected Map<AttributeKey<?>, Attribute<?>> accessor = new ConcurrentHashMap<>();

    @Override
    public <T> T get(AttributeKey<T> key) {
        Attribute<?> attribute = accessor.get(key);
        if (attribute == null || attribute.get() == null) {
            return null;
        }
        return (T) attribute.get();
    }

    @Override
    public <T> void set(AttributeKey<T> key, T value) {
        Attribute<T> attribute = (Attribute<T>) accessor.computeIfAbsent(key, Attribute::new);
        attribute.set(value);
    }

    @Override
    public Accessor remove(AttributeKey<?> key) {
        accessor.remove(key);
        return this;
    }

    @Override
    public void clear() {
        accessor.clear();
    }
}
