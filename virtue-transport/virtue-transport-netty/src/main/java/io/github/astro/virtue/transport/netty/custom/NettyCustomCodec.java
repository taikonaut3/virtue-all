package io.github.astro.virtue.transport.netty.custom;

import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.code.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Getter;

import static io.github.astro.virtue.common.constant.Constant.DEFAULT_MAX_MESSAGE_SIZE;

/**
 * Read the Envelope by data length
 */
public final class NettyCustomCodec {

    @Getter
    private final ChannelHandler encoder;

    @Getter
    private final ChannelHandler decoder;

    private final Codec codec;

    public NettyCustomCodec(URL url, Codec codec) {
        this.codec = codec;
        int maxReceiveSize;
//        if (codec.getEncodedClass() == Request.class) {
//            maxReceiveSize = url.getIntParameter(Key.CLIENT_MAX_RECEIVE_SIZE, DEFAULT_MAX_MESSAGE_SIZE);
//        } else {
//            maxReceiveSize = url.getIntParameter(Key.SERVER_MAX_RECEIVE_SIZE, DEFAULT_MAX_MESSAGE_SIZE);
//        }
        encoder = new NettyEncoder();
        decoder = new NettyDecoder(DEFAULT_MAX_MESSAGE_SIZE);
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

        public NettyDecoder(int maxFrameLength) {
            super(maxFrameLength, 0, 4, 0, 4, true);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
            ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
            if (byteBuf != null) {
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                return codec.decode(bytes);
            }
            return null;
        }

    }

}
