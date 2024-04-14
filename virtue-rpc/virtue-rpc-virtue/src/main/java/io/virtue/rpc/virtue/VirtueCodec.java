package io.virtue.rpc.virtue;

import io.virtue.common.constant.Components;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.CodecException;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.common.util.bytes.ByteReader;
import io.virtue.common.util.bytes.ByteWriter;
import io.virtue.common.util.bytes.HeapByteReader;
import io.virtue.common.util.bytes.HeapByteWriter;
import io.virtue.rpc.virtue.envelope.VirtueEnvelope;
import io.virtue.rpc.virtue.envelope.VirtueRequest;
import io.virtue.rpc.virtue.envelope.VirtueResponse;
import io.virtue.serialization.Serializer;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.compress.Compressor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static io.virtue.common.constant.Components.Serialization.*;
import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Envelope is formed in the following Order:
 * 1、Total Length of message: 4 bytes
 * 2、Magic: 4 bytes
 * 3、Message Type: 4 bytes
 * 4、Compress Type: 1 byte
 * 5、URL Length: 4 bytes
 * 6、URL Data: url_length bytes
 * 7、Body Data: residual bytes
 * <p>
 * +-----------+----------+-------------+--------------+-----------+------------+-------------+
 * | TotalLen  |   Magic  | MessageType | CompressType |  URLLen   |     URL    |    Body     |
 * | (4 bytes) | (1 byte) |  (4 bytes)  |   (1 byte)   | (4 bytes) | (variable) |  (variable) |
 * +-----------+----------+-------------+--------------+-----------+------------+-------------+
 * <p>
 * Especial: Total length Reflected in the network framework (eg: Netty).
 *
 * @see io.virtue.transport.netty.custom.NettyCustomCodec
 */
@ToString
public class VirtueCodec implements Codec {

    private static final Logger logger = LoggerFactory.getLogger(VirtueCodec.class);

    private static final List<String> COMMON_SERIALIZATION = List.of(JSON, MSGPACK, PROTOBUF);

    private static final int MAGIC_REQ = 888;

    private static final int MAGIC_RES = 999;

    private final Class<?> encodedClass;

    private final Class<?> decodedClass;

    private Constructor<? extends VirtueEnvelope> allArgsConstructor;

    private Constructor<? extends VirtueEnvelope> noArgsConstructor;

    public VirtueCodec(Class<? extends VirtueEnvelope> encodedClass, Class<? extends VirtueEnvelope> decodedClass) {
        this.encodedClass = encodedClass;
        this.decodedClass = decodedClass;
        findDecodedConstructor();
    }

    @Override
    public byte[] encode(Object message) throws CodecException {
        try {
            if (message instanceof Request request) {
                return encodeRequest(request);
            } else {
                Response response = (Response) message;
                return encodeResponse(response);
            }
        } catch (Exception e) {
            logger.error("Encode message fail", e);
            throw new CodecException("Encode message fail", e);
        }
    }

    @Override
    public Object decode(byte[] bytes) throws CodecException {
        try {
            if (decodedClass == VirtueRequest.class) {
                return decodeRequest(bytes);
            } else {
                return decodeResponse(bytes);
            }
        } catch (Exception e) {
            logger.error("Decode message fail", e);
            throw new CodecException("Decode message fail", e);
        }
    }

    private byte[] encodeRequest(Request request) {
        VirtueRequest virtueRequest = (VirtueRequest) request.message();
        return encodeEnvelope(MAGIC_REQ, virtueRequest, null);
    }

    private Request decodeRequest(byte[] bytes) throws Exception {
        ByteReader byteReader = new HeapByteReader(bytes);
        // magic
        int magic = byteReader.readInt();
        if (magic != MAGIC_REQ) {
            throw new IllegalArgumentException("Parse protocol error,Magic is not match");
        }
        VirtueRequest virtueRequest = (VirtueRequest) decodeEnvelope(byteReader);
        return new Request(virtueRequest.url(), virtueRequest);
    }

    private byte[] encodeResponse(Response response) {
        VirtueResponse virtueResponse = (VirtueResponse) response.message();
        return encodeEnvelope(MAGIC_RES, virtueResponse, response.code());
    }

    private Response decodeResponse(byte[] bytes) throws Exception {
        ByteReader byteReader = new HeapByteReader(bytes);
        // magic
        int magic = byteReader.readInt();
        if (magic != MAGIC_RES) {
            throw new IllegalArgumentException("Parse protocol error,Magic is not match");
        }
        // code
        byte code = byteReader.readByte();
        VirtueResponse virtueResponse = (VirtueResponse) decodeEnvelope(byteReader);
        return new Response(code, virtueResponse.url(), virtueResponse);
    }

    private byte[] encodeEnvelope(int magic, VirtueEnvelope virtueEnvelope, Byte responseCode) {
        URL url = virtueEnvelope.url();
        // message type
        String envelope = url.getParam(Key.ENVELOPE);
        Mode envelopeMode = ModeContainer.getMode(Key.ENVELOPE, envelope);
        // compress type
        String compression = url.getParam(Key.COMPRESSION);
        Mode compressMode = ModeContainer.getMode(Key.COMPRESSION, compression);
        Compressor compressorInstance = virtueEnvelope.compression();
        /*  ---------------- url ------------------  */
        String urlStr = url.toString();
        byte[] urlBytes = compressorInstance.compress(urlStr.getBytes(StandardCharsets.UTF_8));
        /*  ---------------- body ------------------  */
        byte[] message = encodeBody(virtueEnvelope);
        /* write */
        int capacity = computeCapacity(urlBytes, message);
        ByteWriter byteWriter = new HeapByteWriter(capacity);
        byteWriter.writeInt(magic);
        if (responseCode != null) {
            byteWriter.writeByte(responseCode);
        }
        byteWriter.writeByte(envelopeMode.type());
        byteWriter.writeByte(compressMode.type());
        byteWriter.writeInt(urlBytes.length);
        byteWriter.writeBytes(urlBytes);
        byteWriter.writeBytes(message);
        return byteWriter.toBytes();
    }

    private VirtueEnvelope decodeEnvelope(ByteReader reader) throws Exception {
        // message type
        Mode envelopeMode = ModeContainer.getMode(Key.ENVELOPE, reader.readByte());
        String currentEnvelope = decodedClass == VirtueRequest.class ? Components.Envelope.REQUEST : Components.Envelope.RESPONSE;
        if (!Objects.equals(envelopeMode.name(), currentEnvelope)) {
            throw new IllegalArgumentException("Parse protocol error,Message type not match");
        }
        // compression type
        Mode compressionMode = ModeContainer.getMode(Key.COMPRESSION, reader.readByte());
        Compressor compressorInstance = ExtensionLoader.loadExtension(Compressor.class, compressionMode.name());
        // url
        int urlLength = reader.readInt();
        byte[] urlCompressedBytes = reader.readBytes(urlLength);
        byte[] urlBytes = compressorInstance.decompress(urlCompressedBytes);
        String urlStr = new String(urlBytes);
        URL url = URL.valueOf(urlStr);
        // body
        byte[] bodyBytes = reader.readBytes(reader.readableBytes());
        Object body = decodeBody(url, bodyBytes);
        return createEnvelope(url, body);
    }

    private byte[] encodeBody(VirtueEnvelope virtueEnvelope) {
        Serializer serializer = virtueEnvelope.serializer();
        Compressor compressor = virtueEnvelope.compression();
        byte[] bytes = serializer.serialize(virtueEnvelope.body());
        logger.debug("{}: [serialization: {}],[compression: {}]",
                simpleClassName(this), simpleClassName(serializer), simpleClassName(compressor));
        return compressor.compress(bytes);
    }

    private Object decodeBody(URL url, byte[] bodyBytes) {
        String serializationName = url.getParam(Key.SERIALIZATION);
        String compressionName = url.getParam(Key.COMPRESSION);
        Serializer serializer = ExtensionLoader.loadExtension(Serializer.class, serializationName);
        Compressor compressor = ExtensionLoader.loadExtension(Compressor.class, compressionName);
        Class<?> bodyType;
        try {
            bodyType = Class.forName(url.getParam(Key.BODY_TYPE));
        } catch (ClassNotFoundException e) {
            bodyType = Object.class;
        }
        byte[] decompress = compressor.decompress(bodyBytes);
        logger.debug("{}: [deserialization: {}],[decompression: {}]",
                simpleClassName(this), simpleClassName(serializer), simpleClassName(compressor));
        return serializer.deserialize(decompress, bodyType);
    }

    @SuppressWarnings("unchecked")
    private void findDecodedConstructor() {
        try {
            allArgsConstructor = (Constructor<? extends VirtueEnvelope>) decodedClass.getConstructor(URL.class, Object.class);
        } catch (NoSuchMethodException e) {
            try {
                noArgsConstructor = (Constructor<? extends VirtueEnvelope>) decodedClass.getConstructor();
            } catch (NoSuchMethodException ex) {
                logger.error("Can't find available Constructor,Need Envelope() or Envelope(URL,Object)", e);
            }
        }
    }

    private VirtueEnvelope createEnvelope(URL url, Object body) throws Exception {
        VirtueEnvelope virtueEnvelope;
        if (allArgsConstructor != null) {
            virtueEnvelope = allArgsConstructor.newInstance(url, body);
        } else {
            virtueEnvelope = noArgsConstructor.newInstance();
            virtueEnvelope.url(url);
            virtueEnvelope.body(body);
        }
        return virtueEnvelope;
    }

    private int computeCapacity(byte[] urlBytes, byte[] bodyBytes) {
        int totalLength = urlBytes.length + bodyBytes.length + 10;
        int nearestPowerOfTwo = 1;
        while (nearestPowerOfTwo < totalLength) {
            nearestPowerOfTwo <<= 1;
        }
        return nearestPowerOfTwo;
    }
}
