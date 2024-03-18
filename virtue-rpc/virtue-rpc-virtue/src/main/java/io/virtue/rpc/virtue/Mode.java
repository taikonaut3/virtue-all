package io.virtue.rpc.virtue;

/**
 * Designed only for mapping: string -> byte,
 * Convenient network transmission.
 */
public interface Mode {

    String name();

    byte type();

}
