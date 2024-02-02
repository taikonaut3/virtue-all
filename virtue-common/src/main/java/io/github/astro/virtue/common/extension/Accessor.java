package io.github.astro.virtue.common.extension;

/**
 * Object Accessor.
 *
 * @see AttributeKey
 */
public interface Accessor {

    /**
     * Get Attribute by AttributeKey.
     *
     * @param key
     * @param <T>
     * @return Attribute
     */
    <T> Attribute<T> attribute(AttributeKey<T> key);

    /**
     * Remove Attribute by AttributeKey's name.
     *
     * @param key
     * @return current object
     */
    Accessor remove(String key);

}
