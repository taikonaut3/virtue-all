package io.github.taikonaut3.virtue.serialization.protobuf;

import com.google.protobuf.MessageLite;
import io.github.taikonaut3.virtue.common.exception.SerializationException;
import io.github.taikonaut3.virtue.common.spi.ServiceProvider;
import io.github.taikonaut3.virtue.serialization.Serializer;

import java.lang.reflect.Method;

import static io.github.taikonaut3.virtue.common.constant.Components.Serialize.PROTOBUF;

@ServiceProvider(PROTOBUF)
public class ProtobufSerializer implements Serializer {

    @Override
    public byte[] serialize(Object input) throws SerializationException {
        try {
            if (input instanceof MessageLite message) {
                return message.toByteArray();
            }
            throw new UnsupportedOperationException("Only Support [com.google.protobuf.MessageLite] Type");
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        try {
            if (MessageLite.class.isAssignableFrom(clazz)) {
                Method parseForm = clazz.getDeclaredMethod("parseFrom", byte[].class);
                parseForm.setAccessible(true);
                return (T) parseForm.invoke(null, (Object) bytes);
            }
            throw new UnsupportedOperationException("Only Support [com.google.protobuf.MessageLite] Type");
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
