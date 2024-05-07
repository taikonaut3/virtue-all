package io.virtue.core;

/**
 * Close and check Resource.
 */
public interface Closeable {

    /**
     * Close Resource.
     */
    void close();

    /**
     * Whether the Resource is Active.
     *
     * @return
     */
    boolean isActive();


}
