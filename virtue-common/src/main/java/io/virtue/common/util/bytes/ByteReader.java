package io.virtue.common.util.bytes;

import io.virtue.common.spi.ServiceInterface;

import java.nio.charset.Charset;

import static io.virtue.common.constant.Components.ByteReader.NIO_HEAP;

/**
 * Tool for Reading bytes.
 */
@ServiceInterface(value = NIO_HEAP, constructor = {byte[].class})
public interface ByteReader {

    byte readByte();

    boolean readBoolean();

    short readShort();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    CharSequence readCharSequence(int length);

    CharSequence readCharSequence(int length, Charset charset);

    byte[] readBytes(int length);

    int readableBytes();

}