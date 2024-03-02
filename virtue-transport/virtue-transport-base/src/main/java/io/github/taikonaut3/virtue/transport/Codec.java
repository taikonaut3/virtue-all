package io.github.taikonaut3.virtue.transport;

import io.github.taikonaut3.virtue.common.exception.CodecException;
import io.github.taikonaut3.virtue.common.util.byteutils.ByteReader;
import io.github.taikonaut3.virtue.common.util.byteutils.ByteWriter;
import io.github.taikonaut3.virtue.transport.channel.Channel;

import java.io.IOException;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/1 10:51
 */
public interface Codec {

    /**
     * Encodes the message into a byte array.
     *
     * @param message The message to be encoded.
     * @return The encoded byte array.
     * @throws CodecException
     */
    byte[] encode(Channel channel, ByteWriter byteWriter, Object message) throws IOException;

    /**
     * Decodes the  byte array into a message.
     *
     * @param bytes The byte array to be decoded.
     * @return The decoded message.
     * @throws CodecException
     */
    Object decode(Channel channel, ByteReader byteReader) throws IOException;
}
