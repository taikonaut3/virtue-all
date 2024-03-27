package io.virtue.common.util.bytes;

import io.virtue.common.spi.ServiceInterface;

import java.nio.charset.Charset;

import static io.virtue.common.constant.Components.ByteWriter.NIO_HEAP;

/**
 * Tool for Writing bytes.
 */
@ServiceInterface(value = NIO_HEAP, constructor = {int.class})
public interface ByteWriter {

    void writeByte(byte value);

    void writeBoolean(boolean value);

    void writeShort(short value);

    void writeInt(int value);

    void writeInt(int value, int index);

    void writeLong(long value);

    void writeFloat(float value);

    void writeDouble(double value);

    void writeBytes(byte[] bytes);

    void writeCharSequence(CharSequence value);

    void writeCharSequence(CharSequence value, Charset charset);

    byte[] toBytes();

    default ByteWriter writeHeadInt(int value) {
        writeInt(value, 0);
        return this;
    }

}