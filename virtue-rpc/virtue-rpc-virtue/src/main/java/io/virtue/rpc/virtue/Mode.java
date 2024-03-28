package io.virtue.rpc.virtue;

/**
 * Designed only for mapping: string -> byte,
 * Convenient network transmission.
 */
public interface Mode {

    /**
     * Mapping name.
     *
     * @return
     */
    String name();

    /**
     * Mapping byte.
     *
     * @return
     */
    byte type();

}
