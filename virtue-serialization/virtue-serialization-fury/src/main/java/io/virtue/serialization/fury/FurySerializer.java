package io.virtue.serialization.fury;

import io.fury.Fury;
import io.fury.ThreadSafeFury;
import io.fury.config.Language;
import io.virtue.common.extension.spi.Extension;
import io.virtue.serialization.AbstractSerializer;

import java.lang.reflect.Type;

import static io.virtue.common.constant.Components.Serialization.FURY;

/**
 * 测试出 fury的序列化并不理想.
 */
@Extension(FURY)
public class FurySerializer extends AbstractSerializer {

    private final ThreadSafeFury fury;

    public FurySerializer() {
        fury = Fury.builder().withLanguage(Language.JAVA)
                .requireClassRegistration(false)
                .buildThreadSafeFury();
    }

    @Override
    protected byte[] doSerialize(Object input) throws Exception {
        return fury.serialize(input);
    }

    @Override
    protected Object doDeserialize(byte[] bytes, Type type) throws Exception {
        return fury.deserialize(bytes);
    }

}
