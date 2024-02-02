package io.github.astro.virtue.common.extension;

/**
 * Used store Generic Object.
 *
 * @param <T>
 */
public class Attribute<T> {

    private T attribute;

    /**
     * Get Attribute Object
     *
     * @return Attribute object
     */
    public T get() {
        return attribute;
    }

    /**
     * Set Attribute Object
     *
     * @param attribute
     */
    public void set(T attribute) {
        this.attribute = attribute;
    }
}
