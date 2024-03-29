package io.virtue.serialization.jdk;

import io.virtue.common.exception.SerializationException;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static io.virtue.common.constant.Components.Serialization.JDK;

/**
 * JDK serializer.
 */
@ServiceProvider(value = JDK)
public class JdkSerializer implements Serializer {

    @Override
    public byte[] serialize(Object input) throws SerializationException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(input);
            return outputStream.toByteArray();
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] input, Class<T> type) throws SerializationException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            Object object = objectInputStream.readObject();
            return type.cast(object);
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
    }

}
