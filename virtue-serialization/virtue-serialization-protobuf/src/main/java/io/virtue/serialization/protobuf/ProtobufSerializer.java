package io.virtue.serialization.protobuf;

import com.google.protobuf.MessageLite;
import io.virtue.common.spi.Extension;
import io.virtue.serialization.AbstractSerializer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.virtue.common.constant.Components.Serialization.PROTOBUF;

/**
 * Protobuf Serializer.
 * <p>If there are multiple parameters,
 * only the first one will be serialized as {@link com.google.protobuf.MessageLite},
 * and the same is true for deserialization</p>
 */
@Extension(PROTOBUF)
public class ProtobufSerializer extends AbstractSerializer {

    private final Map<Class<?>, MessageLite> messageMap = new ConcurrentHashMap<>();

    @Override
    protected byte[] doSerialize(Object input) throws Exception {
        if (input instanceof MessageLite message) {
            return message.toByteArray();
        } else {
            throw new UnsupportedOperationException("Only Support [com.google.protobuf.MessageLite] Type");
        }
    }

    @Override
    protected Object doDeserialize(byte[] bytes, Type type) throws Exception {
        if (type instanceof Class<?> classType && MessageLite.class.isAssignableFrom(classType)) {
            MessageLite messageLite = messageMap.get(classType);
            Object result;
            if (messageLite != null) {
                result = messageLite.getParserForType().parseFrom(bytes);
            } else {
                Method parseForm = classType.getDeclaredMethod("parseFrom", byte[].class);
                parseForm.setAccessible(true);
                result = parseForm.invoke(null, new Object[]{bytes});
            }
            return result;
        } else {
            throw new UnsupportedOperationException("Only Support [com.google.protobuf.MessageLite] Type");
        }
    }

    public <T extends MessageLite> void register(Class<T> type, T defaultMessage) {
        messageMap.put(type, defaultMessage);
    }
}
