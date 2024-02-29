package io.github.taikonaut3.virtue.serialization.protobuf;

import com.google.protobuf.Message;
import io.github.taikonaut3.virtue.common.exception.SerializationException;
import io.github.taikonaut3.virtue.common.spi.ServiceProvider;
import io.github.taikonaut3.virtue.common.util.ReflectUtil;
import io.github.taikonaut3.virtue.serialization.Serializer;

import static io.github.taikonaut3.virtue.common.constant.Components.Serialize.PROTOBUF;

@ServiceProvider(PROTOBUF)
public class ProtobufSerializer implements Serializer {

    @Override
    public byte[] serialize(Object input) throws SerializationException {
        try {
            if (input instanceof Message message) {
                return message.toByteArray();
            }
            throw new UnsupportedOperationException("Only Support [com.google.protobuf.Message] Type");
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        try {
            if (Message.class.isAssignableFrom(clazz)) {
                Message defaultMessage = (Message) ReflectUtil.invokeStaticDeclaredMethod(clazz, "getDefaultInstance");
                return (T) defaultMessage.getParserForType().parseFrom(bytes);
            }
            throw new UnsupportedOperationException("Only Support [com.google.protobuf.Message] Type");
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
