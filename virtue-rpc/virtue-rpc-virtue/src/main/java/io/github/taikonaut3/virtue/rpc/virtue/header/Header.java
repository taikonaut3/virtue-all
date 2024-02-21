package io.github.taikonaut3.virtue.rpc.virtue.header;

import io.github.taikonaut3.virtue.common.constant.Key;
import io.github.taikonaut3.virtue.common.constant.Mode;
import io.github.taikonaut3.virtue.common.constant.ModeContainer;
import io.github.taikonaut3.virtue.common.spi.ExtensionLoader;
import io.github.taikonaut3.virtue.serialization.Serializer;

import java.util.Map;

public interface Header {

    int getLength();

    int getAllowMaxSize();

    Mode getEnvelopeMode();

    Mode getProtocolMode();

    Map<String, String> getExtendsData();

    void addExtendData(String key, String value);

    void addExtendData(Map<String, String> extendData);

    String getExtendData(String key);

    byte[] fixDataToBytes();

    byte[] extendDataToBytes();

    default Mode getSerializerMode() {
        String serial = getExtendData(Key.SERIALIZE);
        return ModeContainer.getMode(Key.SERIALIZE, serial);
    }

    default Serializer getSerializer() {
        String serial = getExtendData(Key.SERIALIZE);
        return ExtensionLoader.loadService(Serializer.class, serial);
    }

}
