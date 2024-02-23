package io.github.taikonaut3.virtue.rpc.virtue.header;

import io.github.taikonaut3.virtue.common.constant.Key;
import io.github.taikonaut3.virtue.common.constant.Mode;
import io.github.taikonaut3.virtue.common.constant.ModeContainer;
import io.github.taikonaut3.virtue.transport.byteutils.ByteReader;
import io.github.taikonaut3.virtue.transport.byteutils.ByteWriter;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(fluent = true)
public class VirtueHeader implements Header {

    private static final int magic = 888;

    private Mode protocolMode;

    private Mode envelopeMode;

    private Mode serializeMode;

    private Mode compressionMode;

    private volatile Map<String, String> extendData;

    public VirtueHeader(Mode protocolMode, Mode envelopeMode, Mode serializeMode, Mode compressionMode) {
        this.protocolMode = protocolMode;
        this.envelopeMode = envelopeMode;
        this.serializeMode = serializeMode;
        this.compressionMode = compressionMode;
        this.extendData = new HashMap<>();
    }

    public static VirtueHeader parse(byte[] bytes) {
        ByteReader reader = ByteReader.newReader(bytes);
        // 魔数位
        int magic = reader.readInt();
        if (VirtueHeader.magic != magic) {
            throw new IllegalArgumentException("parse ProtocolHeader error");
        }
        // 协议版本
        Mode protocolMode = ModeContainer.getMode(Key.PROTOCOL, reader.readByte());
        // 消息类型
        Mode envelopeMode = ModeContainer.getMode(Key.ENVELOPE, reader.readByte());
        // 序列化方式
        Mode serializeMode = ModeContainer.getMode(Key.SERIALIZE, reader.readByte());
        // 压缩方式
        Mode compressionMode = ModeContainer.getMode(Key.COMPRESSION, reader.readByte());
        return new VirtueHeader(protocolMode, envelopeMode, serializeMode, compressionMode);
    }

    @Override
    public int allowMaxSize() {
        return 0;
    }

    @Override
    public int length() {
        return fixDataToBytes().length + extendDataToBytes().length;
    }

    @Override
    public void addExtendData(Map<String, String> extendData) {
        this.extendData = extendData;
    }

    @Override
    public void addExtendData(String key, String value) {
        extendData.put(key, value);
    }

    @Override
    public Map<String, String> extendsData() {
        return extendData;
    }

    @Override
    public String getExtendData(String key) {
        return extendData.get(key);
    }

    @Override
    public byte[] fixDataToBytes() {
        ByteWriter writer = ByteWriter.newWriter();
        // 魔数位
        writer.writeInt(magic);
        // 协议版本
        writer.writeByte(protocolMode().type());
        // 消息类型
        writer.writeByte(envelopeMode().type());
        // 序列化方式
        writer.writeByte(serializeMode().type());
        // 压缩方式
        writer.writeByte(compressionMode().type());
        return writer.toBytes();
    }

    @Override
    public byte[] extendDataToBytes() {
        return serializer().serialize(extendData);
    }

}
