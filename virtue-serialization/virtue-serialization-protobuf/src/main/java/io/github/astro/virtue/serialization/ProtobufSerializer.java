package io.github.astro.virtue.serialization;

import io.github.astro.virtue.common.exception.SerializationException;

public class ProtobufSerializer implements Serializer{
    @Override
    public byte[] serialize(Object input) throws SerializationException {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        return null;
    }
}
