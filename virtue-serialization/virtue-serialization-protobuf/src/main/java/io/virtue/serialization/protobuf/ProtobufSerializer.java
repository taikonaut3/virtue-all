package io.virtue.serialization.protobuf;

import com.google.protobuf.MessageLite;
import io.virtue.common.spi.Extension;
import io.virtue.serialization.AbstractSerializer;

import java.lang.reflect.Method;
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

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T doDeserialize(byte[] bytes, Class<T> type) throws Exception {
        if (MessageLite.class.isAssignableFrom(type)) {
            MessageLite messageLite = messageMap.get(type);
            T result;
            if (messageLite != null) {
                result = (T) messageLite.getParserForType().parseFrom(bytes);
            } else {
                Method parseForm = type.getDeclaredMethod("parseFrom", byte[].class);
                parseForm.setAccessible(true);
                result = (T) parseForm.invoke(null, new Object[]{bytes});
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
