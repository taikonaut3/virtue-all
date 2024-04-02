package io.virtue.common.util.bytes;

import java.nio.charset.Charset;

/**
 * Tool for Reading bytes.
 * <p>Default use {@link io.virtue.rpc.support.HeapByteReader}</p>
 */
public interface ByteReader {

    /**
     * Read a byte.
     *
     * @return
     */
    byte readByte();

    /**
     * Read a boolean value.
     *
     * @return
     */
    boolean readBoolean();

    /**
     * Read a short value.
     *
     * @return
     */
    short readShort();

    /**
     * Read an int value.
     *
     * @return
     */
    int readInt();

    /**
     * Read a long value.
     *
     * @return
     */
    long readLong();

    /**
     * Read a float value.
     *
     * @return
     */
    float readFloat();

    /**
     * Read a double value.
     *
     * @return
     */
    double readDouble();

    /**
     * Read a string, the length of which is specified by the parameter length.
     *
     * @param length
     * @return
     */
    CharSequence readCharSequence(int length);

    /**
     * Read a string, the length of which is specified by the parameter length and the charset character set.
     *
     * @param length
     * @param charset
     * @return
     */
    CharSequence readCharSequence(int length, Charset charset);

    /**
     * Read a byte array, the length of which is specified by the parameter length.
     *
     * @param length
     * @return
     */
    byte[] readBytes(int length);

    /**
     * The number of readable bytes.
     *
     * @return
     */
    int readableBytes();

}