package io.virtue.common.extension;

/**
 * Object accessor.
 *
 * @see AttributeKey
 */
public interface Accessor {

    /**
     * Get attribute value by attributeKey.
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> T get(AttributeKey<T> key);

    /**
     * Set attribute value by attributeKey.
     *
     * @param key
     * @param value
     * @param <T>
     */
    <T> void set(AttributeKey<T> key, T value);

    /**
     * Remove attribute by attributeKey.
     *
     * @param key
     * @return
     */
    Accessor remove(AttributeKey<?> key);

    /**
     * Remove all attributes.
     */
    void clear();

}
