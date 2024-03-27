package io.virtue.rpc.support;

import io.virtue.common.spi.Scope;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.util.bytes.ByteReader;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static io.virtue.common.constant.Components.ByteReader.NIO_HEAP;

/**
 * Use HeapByteBuffer asReadOnlyBuffer.
 */
@ServiceProvider(value = NIO_HEAP, scope = Scope.PROTOTYPE)
public class HeapByteReader implements ByteReader {

    private final ByteBuffer buffer;

    public HeapByteReader(byte[] bytes) {
        buffer = ByteBuffer.wrap(bytes);
        buffer.asReadOnlyBuffer();
    }

    @Override
    public byte readByte() {
        return buffer.get();
    }

    @Override
    public boolean readBoolean() {
        return buffer.get() != 0;
    }

    @Override
    public short readShort() {
        return buffer.getShort();
    }

    @Override
    public int readInt() {
        return buffer.getInt();
    }

    @Override
    public long readLong() {
        return buffer.getLong();
    }

    @Override
    public float readFloat() {
        return buffer.getFloat();
    }

    @Override
    public double readDouble() {
        return buffer.getDouble();
    }

    @Override
    public CharSequence readCharSequence(int length) {
        return readCharSequence(length, StandardCharsets.UTF_8);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return new String(readBytes(length), charset);
    }

    @Override
    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }

    @Override
    public int readableBytes() {
        return buffer.remaining();
    }
}
