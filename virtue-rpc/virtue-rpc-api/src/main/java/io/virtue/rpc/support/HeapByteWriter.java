package io.virtue.rpc.support;

import io.virtue.common.spi.Scope;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.util.bytes.ByteWriter;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static io.virtue.common.constant.Components.ByteWriter.NIO_HEAP;

/**
 * Use HeapByteBuffer.
 */
@ServiceProvider(value = NIO_HEAP, scope = Scope.PROTOTYPE)
public class HeapByteWriter implements ByteWriter {

    private final ByteBuffer buffer;

    public HeapByteWriter(int capacity) {
        buffer = ByteBuffer.allocate(capacity);
    }

    @Override
    public void writeByte(byte value) {
        buffer.put(value);
    }

    @Override
    public void writeBoolean(boolean value) {
        buffer.put((byte) (value ? 1 : 0));
    }

    @Override
    public void writeShort(short value) {
        buffer.putShort(value);
    }

    @Override
    public void writeInt(int value) {
        buffer.putInt(value);
    }

    @Override
    public void writeInt(int value, int index) {
        buffer.putInt(index, value);
    }

    @Override
    public void writeLong(long value) {
        buffer.putLong(value);
    }

    @Override
    public void writeFloat(float value) {
        buffer.putFloat(value);
    }

    @Override
    public void writeDouble(double value) {
        buffer.putDouble(value);
    }

    @Override
    public void writeBytes(byte[] bytes) {
        buffer.put(bytes);
    }

    @Override
    public void writeCharSequence(CharSequence value) {
        writeCharSequence(value, StandardCharsets.UTF_8);
    }

    @Override
    public void writeCharSequence(CharSequence value, Charset charset) {
        byte[] bytes = value.toString().getBytes(charset);
        buffer.put(bytes);
    }

    @Override
    public byte[] toBytes() {
        return buffer.array();
    }
}
