package io.github.astro.virtue.serialization.fury;

import io.fury.Fury;
import io.fury.ThreadSafeFury;
import io.fury.config.Language;
import io.github.astro.virtue.common.exception.SerializationException;
import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.serialization.Serializer;

import static io.github.astro.virtue.common.constant.Components.Serialize.FURY;

/**
 * 测试出 fury的序列化并不理想
 */
@ServiceProvider(FURY)
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
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        return (T) fury.deserialize(bytes);
    }

}
