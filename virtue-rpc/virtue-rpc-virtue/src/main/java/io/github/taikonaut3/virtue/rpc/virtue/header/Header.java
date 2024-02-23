package io.github.taikonaut3.virtue.rpc.virtue.header;

import io.github.taikonaut3.virtue.common.constant.Mode;
import io.github.taikonaut3.virtue.common.spi.ExtensionLoader;
import io.github.taikonaut3.virtue.serialization.Serializer;
import io.github.taikonaut3.virtue.transport.compress.Compression;

import java.util.Map;

public interface Header {

    int length();

    int allowMaxSize();

    Mode envelopeMode();

    Mode protocolMode();

    Mode serializeMode();

    Mode compressionMode();

    Map<String, String> extendsData();

    void addExtendData(String key, String value);

    void addExtendData(Map<String, String> extendData);

    String getExtendData(String key);

    byte[] fixDataToBytes();

    byte[] extendDataToBytes();

    default Serializer serializer() {
        return ExtensionLoader.loadService(Serializer.class, serializeMode().name());
    }

    default Compression compression() {
        return ExtensionLoader.loadService(Compression.class, compressionMode().name());
    }

}
