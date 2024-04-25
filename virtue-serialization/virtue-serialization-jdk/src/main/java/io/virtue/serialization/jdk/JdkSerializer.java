package io.virtue.serialization.jdk;

import io.virtue.common.spi.Extension;
import io.virtue.serialization.AbstractSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;

import static io.virtue.common.constant.Components.Serialization.JDK;

/**
 * JDK serializer.
 */
@Extension(JDK)
public class JdkSerializer extends AbstractSerializer {

    @Override
    protected byte[] doSerialize(Object input) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(input);
            return outputStream.toByteArray();
        }
    }

    @Override
    protected Object doDeserialize(byte[] bytes, Type type) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            return objectInputStream.readObject();
        }
    }

}
