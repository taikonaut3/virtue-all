package io.virtue.serialization.fury;

import io.fury.Fury;
import io.fury.ThreadSafeFury;
import io.fury.config.Language;
import io.virtue.common.constant.Components;
import io.virtue.common.exception.SerializationException;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.serialization.Serializer;

/**
 * 测试出 fury的序列化并不理想
 */
@ServiceProvider(Components.Serialization.FURY)
public class FurySerializer implements Serializer {

    private final ThreadSafeFury fury;

    public FurySerializer() {
        fury = Fury.builder().withLanguage(Language.JAVA)
                .requireClassRegistration(false)
                .buildThreadSafeFury();
    }

    @Override
    public byte[] serialize(Object input) throws SerializationException {
        return fury.serialize(input);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> type) throws SerializationException {
        return (T) fury.deserialize(bytes);
    }

}
