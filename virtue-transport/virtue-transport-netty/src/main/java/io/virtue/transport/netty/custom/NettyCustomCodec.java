package io.virtue.transport.netty.custom;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.codec.Codec;
import lombok.Getter;

/**
 * Read the Envelope by data length.
 */
public final class NettyCustomCodec {

    @Getter
    private final ChannelHandler encoder;

    @Getter
    private final ChannelHandler decoder;

    private final Codec codec;

    public NettyCustomCodec(URL url, Codec codec, boolean isServer) {
        this.codec = codec;
        String maxMessageKey = isServer ? Key.MAX_RECEIVE_SIZE : Key.CLIENT_MAX_RECEIVE_SIZE;
        int maxReceiveSize = url.getIntParam(maxMessageKey, Constant.DEFAULT_MAX_MESSAGE_SIZE);
        encoder = new NettyEncoder();
        decoder = new NettyDecoder(maxReceiveSize);
    }

    class NettyEncoder extends MessageToByteEncoder<Object> {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
            byte[] bytes = codec.encode(message);
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }

    }

    class NettyDecoder extends LengthFieldBasedFrameDecoder {

        NettyDecoder(int maxFrameLength) {
            // First int is total length
            super(maxFrameLength, 0, 4, 0, 4, true);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
            ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
            if (byteBuf != null) {
                // 这里会拷贝数据
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                return codec.decode(bytes);
            }
            return null;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }
    }

}
