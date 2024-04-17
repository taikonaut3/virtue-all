package io.virtue.common.extension;

/**
 * Ability to create a copy of the exact same content.
 */
public interface Replicable<T> {

    /**
     * Returns a deep copy of the object.
     *
     * @return
     */
    T replicate();
}
