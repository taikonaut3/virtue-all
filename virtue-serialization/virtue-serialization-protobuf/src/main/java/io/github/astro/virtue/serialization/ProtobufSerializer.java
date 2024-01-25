package io.github.astro.virtue.serialization;

import io.github.astro.virtue.common.exception.SerializationException;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/25 11:10
 */
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
