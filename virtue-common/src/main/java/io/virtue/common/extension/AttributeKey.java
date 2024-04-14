package io.virtue.common.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Used get attribute by generic.
 *
 * @param <T>
 */
public final class AttributeKey<T> {

    private static final Map<CharSequence, AttributeKey<?>> KEY_POOL = new ConcurrentHashMap<>();

    private final CharSequence name;

    private AttributeKey(CharSequence name) {
        this.name = name;
    }

    /**
     * Get an attributeKey by name, if it doesn't exist, create it and put it into the pool.
     *
     * @param name
     * @param <T>
     * @return attributeKey instance
     */
    @SuppressWarnings("unchecked")
    public static <T> AttributeKey<T> of(CharSequence name) {
        AttributeKey<?> key = KEY_POOL.get(name);
        if (key == null) {
            key = new AttributeKey<T>(name);
            KEY_POOL.put(name, key);
        }
        return (AttributeKey<T>) key;
    }

    /**
     * Get attribute by accessor.
     *
     * @param accessor
     * @return
     */
    public T get(Accessor accessor) {
        return accessor.get(this);
    }

    public void set(Accessor accessor, T value) {
        accessor.set(this, value);
    }

    /**
     * key name.
     *
     * @return
     */
    public CharSequence name() {
        return name;
    }

}
