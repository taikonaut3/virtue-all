package io.virtue.serialization;

import io.virtue.common.exception.SerializationException;
import io.virtue.common.spi.ServiceInterface;

import static io.virtue.common.constant.Components.Serialize.KRYO;

/**
 * Serializer interface that extends the Converter interface.
 */
@ServiceInterface(KRYO)
public interface Serializer extends Converter {

    /**
     * Serializes the input object into a byte array.
     *
     * @param input The object to serialize.
     * @return The serialized byte array.
     * @throws SerializationException if an error occurs during serialization.
     */
    byte[] serialize(Object input) throws SerializationException;

    /**
     * Deserializes a byte array into an object of the specified class.
     *
     * @param bytes The byte array to deserialize.
     * @param type The class of the object to deserialize.
     * @param <T>   The type of the object to deserialize.
     * @return The deserialized object.
     * @throws SerializationException if an error occurs during deserialization.
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws SerializationException;
}

