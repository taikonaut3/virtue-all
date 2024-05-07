package io.virtue.transport.netty;

import io.netty.buffer.ByteBuf;

/**
 * ByteBufUtil
 */
public class ByteBufUtil {

    public static byte[] getBytes(ByteBuf buf) {
        return io.netty.buffer.ByteBufUtil.getBytes(buf, buf.readerIndex(), buf.readableBytes(), false);
    }

}
