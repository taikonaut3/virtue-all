package io.virtue.serialization;

import io.virtue.common.exception.SerializationException;
import io.virtue.common.extension.spi.Extensible;

import java.lang.reflect.Type;

import static io.virtue.common.constant.Components.Serialization.KRYO;

/**
 * Serialization interface that extends the Converter interface.
 */
@Extensible(KRYO)
public interface Serializer extends Converter {

    /**
     * Serialize the input object into a byte array.
     *
     * @param input The object to serialization.
     * @return The serialize byte array.
     * @throws SerializationException if an error occurs during serialization.
     */
    byte[] serialize(Object input) throws SerializationException;

    /**
     * Deserialize a byte array into an object of the specified class.
     *
     * @param bytes The byte array to deserialization.
     * @param type  The class of the object to deserialization.
     * @param <T>   The type of the object to deserialization.
     * @return The deserialize object.
     * @throws SerializationException if an error occurs during deserialization.
     */
    <T> T deserialize(byte[] bytes, Type type) throws SerializationException;
}

