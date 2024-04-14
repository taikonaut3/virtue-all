package io.virtue.common.extension;

/**
 * Used store Generic Object.
 *
 * @param <T>
 */
public class Attribute<T> {

    private final AttributeKey<T> key;

    public Attribute(AttributeKey<T> key) {
        this.key = key;
    }

    private T attribute;

    /**
     * Get attribute object.
     *
     * @return attribute object
     */
    public T get() {
        return attribute;
    }

    /**
     * Set attribute object.
     *
     * @param attribute
     */
    public void set(T attribute) {
        this.attribute = attribute;
    }

    /**
     * Get attribute object,if attribute is null then remove it from accessor.
     *
     * @param accessor
     * @return
     */
    public T get(Accessor accessor) {
        if (attribute == null) {
            accessor.remove(key);
        }
        return get();
    }
}
