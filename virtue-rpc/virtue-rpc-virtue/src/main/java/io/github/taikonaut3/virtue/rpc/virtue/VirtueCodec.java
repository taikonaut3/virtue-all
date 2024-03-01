package io.github.taikonaut3.virtue.rpc.virtue;

import io.github.taikonaut3.virtue.common.constant.Components;
import io.github.taikonaut3.virtue.common.constant.Key;
import io.github.taikonaut3.virtue.common.constant.Mode;
import io.github.taikonaut3.virtue.common.constant.ModeContainer;
import io.github.taikonaut3.virtue.common.exception.CodecException;
import io.github.taikonaut3.virtue.common.spi.ExtensionLoader;
import io.github.taikonaut3.virtue.common.url.URL;
import io.github.taikonaut3.virtue.common.util.byteutils.ByteReader;
import io.github.taikonaut3.virtue.common.util.byteutils.ByteWriter;
import io.github.taikonaut3.virtue.rpc.virtue.envelope.VirtueEnvelope;
import io.github.taikonaut3.virtue.rpc.virtue.envelope.VirtueRequest;
import io.github.taikonaut3.virtue.rpc.virtue.envelope.VirtueResponse;
import io.github.taikonaut3.virtue.serialization.Serializer;
import io.github.taikonaut3.virtue.transport.Request;
import io.github.taikonaut3.virtue.transport.Response;
import io.github.taikonaut3.virtue.transport.codec.Codec;
import io.github.taikonaut3.virtue.transport.compress.Compression;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Envelope is formed in the following Order:
 * 1、Total length of Envelope: 4 bytes
 * 2、Total length of Header: 4 bytes
 * 3、Fix length of Header: 4 bytes
 * 4、Header Data
 * 5、Body Data
 * <p>
 * +-----------+---------- +--------------+-------------+------------+
 * | TotalLen  | HeaderLen | FixHeaderLen | Header Data | Body Data  |
 * | (4 bytes) | (4 bytes) |  (4 bytes)   | (variable)  | (variable) |
 * +-----------+----------+---------------+-------------+------------+
 * <p>
 * Especial: Total length Reflected in the network framework (eg: Netty)
 *
 * @see io.github.taikonaut3.virtue.transport.netty.custom.NettyCustomCodec
 */
@ToString
public class VirtueCodec implements Codec {

    private static final Logger logger = LoggerFactory.getLogger(VirtueCodec.class);

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
            throw new CodecException(e);
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
            throw new CodecException(e);
        }
    }

    private byte[] encodeRequest(Request request) {
        ByteWriter byteWriter = ByteWriter.newWriter();
        VirtueRequest virtueRequest = (VirtueRequest) request.message();
        // magic
        byteWriter.writeInt(MAGIC_REQ);
        encodeEnvelope(byteWriter, virtueRequest);
        return byteWriter.toBytes();
    }

    private Request decodeRequest(byte[] bytes) {
        ByteReader byteReader = ByteReader.newReader(bytes);
        // magic
        int magic = byteReader.readInt();
        if (magic != MAGIC_REQ) {
            throw new IllegalArgumentException("Parse protocol error,Magic is not match");
        }
        VirtueRequest virtueRequest = (VirtueRequest) decodeEnvelope(byteReader);
        return new Request(virtueRequest.url(), virtueRequest);
    }

    private byte[] encodeResponse(Response response) {
        ByteWriter byteWriter = ByteWriter.newWriter();
        VirtueResponse virtueResponse = (VirtueResponse) response.message();
        // magic
        byteWriter.writeInt(MAGIC_RES);
        // code
        byteWriter.writeByte(response.code());
        encodeEnvelope(byteWriter, virtueResponse);
        return byteWriter.toBytes();
    }

    private Response decodeResponse(byte[] bytes) {
        ByteReader byteReader = ByteReader.newReader(bytes);
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

    private void encodeEnvelope(final ByteWriter writer, VirtueEnvelope virtueEnvelope) {
        URL url = virtueEnvelope.url();
        // message type
        String envelope = url.getParameter(Key.ENVELOPE);
        Mode envelopeMode = ModeContainer.getMode(Key.ENVELOPE, envelope);
        writer.writeByte(envelopeMode.type());
        // compress type
        String compression = url.getParameter(Key.COMPRESSION);
        Mode compressMode = ModeContainer.getMode(Key.COMPRESSION, compression);
        writer.writeByte(compressMode.type());
        Compression compressionInstance = virtueEnvelope.compression();
        /*  ---------------- url ------------------  */
        String urlStr = url.toString();
        byte[] urlBytes = compressionInstance.compress(urlStr.getBytes(StandardCharsets.UTF_8));
        // url length
        writer.writeInt(urlBytes.length);
        // url bytes
        writer.writeBytes(urlBytes);
        /*  ---------------- body ------------------  */
        byte[] message = encodeBody(virtueEnvelope);
        writer.writeBytes(message);
    }

    private VirtueEnvelope decodeEnvelope(ByteReader reader) throws CodecException {
        try {
            // message type
            Mode envelopeMode = ModeContainer.getMode(Key.ENVELOPE, reader.readByte());
            String currentEnvelope = decodedClass == VirtueRequest.class ? Components.Envelope.REQUEST : Components.Envelope.RESPONSE;
            if (!Objects.equals(envelopeMode.name(), currentEnvelope)) {
                throw new IllegalArgumentException("Parse protocol error,Message type not match");
            }
            // compression type
            Mode compressionMode = ModeContainer.getMode(Key.COMPRESSION, reader.readByte());
            Compression compressionInstance = ExtensionLoader.loadService(Compression.class, compressionMode.name());
            // url
            int urlLength = reader.readInt();
            byte[] urlCompressedBytes = reader.readBytes(urlLength);
            byte[] urlBytes = compressionInstance.decompress(urlCompressedBytes);
            String urlStr = new String(urlBytes);
            URL url = URL.valueOf(urlStr);
            // body
            byte[] bodyBytes = reader.readBytes(reader.readableBytes());
            Object body = decodeBody(url, bodyBytes);
            return createEnvelope(url, body);
        } catch (Exception e) {
            logger.error(this.getClass().getSimpleName() + " decode Error", e);
            throw new CodecException(e);
        }
    }

    private byte[] encodeBody(VirtueEnvelope virtueEnvelope) {
        try {
            Serializer serializer = virtueEnvelope.serializer();
            Compression compression = virtueEnvelope.compression();
            byte[] bytes = serializer.serialize(virtueEnvelope.body());
            logger.debug("{}: [serializer: {}],[compression: {}]", this.getClass().getSimpleName(), serializer.getClass().getSimpleName(), compression.getClass().getSimpleName());
            return compression.compress(bytes);
        } catch (Exception e) {
            logger.error(this.getClass().getSimpleName() + " encode Error", e);
            throw new CodecException(e);
        }
    }

    private Object decodeBody(URL url, byte[] bodyBytes) {
        String serialize = url.getParameter(Key.SERIALIZE);
        String compression = url.getParameter(Key.COMPRESSION);
        Serializer serializer = ExtensionLoader.loadService(Serializer.class, serialize);
        Compression compressionInstance = ExtensionLoader.loadService(Compression.class, compression);
        Class<?> bodyType;
        try {
            bodyType = Class.forName(url.getParameter(Key.BODY_TYPE));
        } catch (ClassNotFoundException e) {
            bodyType = Object.class;
        }
        byte[] decompress = compressionInstance.decompress(bodyBytes);
        logger.debug("{}: [deserializer: {}],[decompression: {}]", this.getClass().getSimpleName(), serializer.getClass().getSimpleName(), compression.getClass().getSimpleName());
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
                logger.error("Can't find available Constructor,Need Envelope() or Envelope(Header,Object)", e);
            }
        }
    }

    private VirtueEnvelope createEnvelope(URL url, Object body) {
        VirtueEnvelope virtueEnvelope;
        try {
            if(allArgsConstructor!=null){
                virtueEnvelope = allArgsConstructor.newInstance(url, body);
            }else {
                virtueEnvelope = noArgsConstructor.newInstance();
                virtueEnvelope.url(url);
                virtueEnvelope.body(body);
            }
        } catch (Exception e) {
            logger.error("Create "+decodedClass.getSimpleName()+" Instance Error ", e);
            throw new CodecException(e);
        }
        return virtueEnvelope;
    }
}
