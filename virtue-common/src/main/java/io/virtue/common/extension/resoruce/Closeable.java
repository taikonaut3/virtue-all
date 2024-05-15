package io.virtue.common.extension.resoruce;

/**
 * Close and check resource.
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
