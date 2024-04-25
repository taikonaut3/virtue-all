package io.virtue.serialization;

import io.virtue.common.exception.SerializationException;

import java.lang.reflect.Type;

/**
 * Abstract Serializer.
 */
public abstract class AbstractSerializer implements Serializer {
    @Override
    public byte[] serialize(Object input) throws SerializationException {
        if (input == null) {
            return new byte[0];
        }
        try {
            return doSerialize(input);
        } catch (Exception e) {
            throw new SerializationException("Failed to serialize object " + input.getClass(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] bytes, Type type) throws SerializationException {
        if (bytes == null || bytes.length == 0)
            return null;
        try {
            return (T) doDeserialize(bytes, type);
        } catch (Exception e) {
            throw new SerializationException("Failed to deserialize bytes to " + type, e);
        }
    }

    protected abstract byte[] doSerialize(Object input) throws Exception;

    protected abstract Object doDeserialize(byte[] bytes, Type type) throws Exception;

}
