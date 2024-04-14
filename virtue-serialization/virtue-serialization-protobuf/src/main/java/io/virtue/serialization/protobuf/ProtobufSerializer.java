package io.virtue.serialization.protobuf;

import com.google.protobuf.MessageLite;
import io.virtue.common.exception.ConversionException;
import io.virtue.common.spi.Extension;
import io.virtue.core.Invocation;
import io.virtue.core.support.TransferableInvocation;
import io.virtue.serialization.AbstractSerializer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static io.virtue.common.constant.Components.Serialization.PROTOBUF;

/**
 * Protobuf Serializer.
 * <p>If there are multiple parameters,
 * only the first one will be serialized as {@link com.google.protobuf.MessageLite},
 * and the same is true for deserialization</p>
 */
@Extension(PROTOBUF)
public class ProtobufSerializer extends AbstractSerializer {

    @Override
    protected byte[] doSerialize(Object input) throws Exception {
        if (input instanceof MessageLite message) {
            return message.toByteArray();
        } else if (input instanceof Invocation invocation) {
            for (Object arg : invocation.args()) {
                if (arg instanceof MessageLite messageLite) {
                    return serialize(messageLite);
                }
            }
        }
        throw new UnsupportedOperationException("Only Support [com.google.protobuf.MessageLite] Type");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T doDeserialize(byte[] bytes, Class<T> type) throws Exception {
        if (Invocation.class.isAssignableFrom(type)) {
            TransferableInvocation invocation = new TransferableInvocation();
            invocation.setArgs(new Object[]{bytes});
            return (T) invocation;
        }
        return (T) bytes;
    }

    @Override
    public Object[] convert(Object[] args, Type[] type) throws ConversionException {
        Object object = convert(args[0], type[0]);
        return new Object[]{object};
    }

    @Override
    public Object convert(Object arg, Type type) throws ConversionException {
        if (arg instanceof byte[] bytes && type instanceof Class<?> typeClass) {
            try {
                if (MessageLite.class.isAssignableFrom(typeClass)) {
                    Method parseForm = typeClass.getDeclaredMethod("parseFrom", byte[].class);
                    parseForm.setAccessible(true);
                    return parseForm.invoke(null, new Object[]{bytes});
                }
                throw new UnsupportedOperationException("Only Support [com.google.protobuf.MessageLite] Type");
            } catch (Exception e) {
                throw new ConversionException(e);
            }
        }
        return arg;
    }
}
