package io.virtue.common.extension;

import io.virtue.common.extension.resoruce.Cleanable;

/**
 * Object accessor.
 *
 * @see AttributeKey
 */
public interface Accessor extends Cleanable {

    /**
     * Get attribute value by attributeKey.
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> T get(AttributeKey<T> key);

    <T> T getOrSet(AttributeKey<T> key, T value);

    /**
     * Set attribute value by attributeKey.
     *
     * @param key
     * @param value
     * @param <T>
     */
    <T> Attribute<T> set(AttributeKey<T> key, T value);

    /**
     * Remove attribute by attributeKey.
     *
     * @param key
     * @return
     */
    Accessor remove(AttributeKey<?> key);

}
