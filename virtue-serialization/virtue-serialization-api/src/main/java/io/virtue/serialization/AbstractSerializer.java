package io.virtue.serialization;

import io.virtue.common.exception.SerializationException;

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

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws SerializationException {
        if (bytes == null || bytes.length == 0)
            return null;
        try {
            return doDeserialize(bytes, type);
        } catch (Exception e) {
            throw new SerializationException("Failed to deserialize bytes to " + type, e);
        }
    }

    protected abstract byte[] doSerialize(Object input) throws Exception;

    protected abstract <T> T doDeserialize(byte[] bytes, Class<T> type) throws Exception;

}
