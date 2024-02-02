package io.github.astro.virtue.common.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeKey<T> {

    private static final Map<String, AttributeKey<?>> KEY_POOL = new ConcurrentHashMap<>();

    private final String name;

    private AttributeKey(String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public static <T> AttributeKey<T> get(String name) {
        AttributeKey<?> key = KEY_POOL.get(name);
        if (key == null) {
            key = new AttributeKey<T>(name);
            KEY_POOL.put(name, key);
        }
        return (AttributeKey<T>) key;
    }

    public String name() {
        return name;
    }

}
