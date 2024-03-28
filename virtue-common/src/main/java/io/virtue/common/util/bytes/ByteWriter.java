package io.virtue.common.util.bytes;

import java.nio.charset.Charset;

/**
 * Tool for Writing bytes.
 * <p>Default use {@link io.virtue.rpc.support.HeapByteWriter}.</p>
 */
public interface ByteWriter {

    /**
     * Write a byte to the buffer.
     *
     * @param value
     */
    void writeByte(byte value);

    /**
     * Write a boolean to the buffer.
     *
     * @param value
     */
    void writeBoolean(boolean value);

    /**
     * Write a short to the buffer.
     *
     * @param value
     */
    void writeShort(short value);

    /**
     * Write an int to the buffer.
     *
     * @param value
     */
    void writeInt(int value);

    /**
     * Write an int to the buffer at the specified index.
     *
     * @param value
     * @param index
     */
    void writeInt(int value, int index);

    /**
     * Write a long to the buffer.
     *
     * @param value
     */
    void writeLong(long value);

    /**
     * Write a float to the buffer.
     *
     * @param value
     */
    void writeFloat(float value);

    /**
     * Write a double to the buffer.
     *
     * @param value
     */
    void writeDouble(double value);

    /**
     * Write a byte array to the buffer.
     *
     * @param bytes
     */
    void writeBytes(byte[] bytes);

    /**
     * Write a char sequence to the buffer.
     *
     * @param value
     */
    void writeCharSequence(CharSequence value);

    /**
     * Write a char sequence to the buffer with the charset character set.
     *
     * @param value
     * @param charset
     */
    void writeCharSequence(CharSequence value, Charset charset);

    /**
     * Write the buffer to a byte array.
     *
     * @return
     */
    byte[] toBytes();

}