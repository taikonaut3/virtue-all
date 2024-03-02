package io.github.taikonaut3.virtue.serialization.protobuf;

import com.google.protobuf.MessageLite;
import io.github.taikonaut3.virtue.common.exception.ConversionException;
import io.github.taikonaut3.virtue.common.exception.SerializationException;
import io.github.taikonaut3.virtue.common.spi.ServiceProvider;
import io.github.taikonaut3.virtue.config.CallArgs;
import io.github.taikonaut3.virtue.config.RpcCallArgs;
import io.github.taikonaut3.virtue.serialization.Serializer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static io.github.taikonaut3.virtue.common.constant.Components.Serialize.PROTOBUF;

@ServiceProvider(PROTOBUF)
public class ProtobufSerializer implements Serializer {

    @Override
    public byte[] serialize(Object input) throws SerializationException {
        try {
            if (input instanceof MessageLite message) {
                return message.toByteArray();
            } else if (input instanceof CallArgs callArgs) {
                for (Object arg : callArgs.args()) {
                    if (arg instanceof MessageLite messageLite) {
                        return serialize(messageLite);
                    }
                }
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
            if (CallArgs.class.isAssignableFrom(clazz)) {
                RpcCallArgs callArgs = new RpcCallArgs();
                callArgs.args(new Object[]{bytes});
                return (T) callArgs;
            }
            return (T) bytes;
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public Object[] convert(Object[] args, Type[] type) throws ConversionException {
        Object object = convert(args[0], type[0]);
        return new Object[]{object};
    }

    @Override
    public Object convert(Object arg, Type type) throws ConversionException {
        if (arg instanceof byte[] bytes && type instanceof Class<?> clazz) {
            try {
                if (MessageLite.class.isAssignableFrom(clazz)) {
                    Method parseForm = clazz.getDeclaredMethod("parseFrom", byte[].class);
                    parseForm.setAccessible(true);
                    return parseForm.invoke(null, (Object) bytes);
                }
                throw new UnsupportedOperationException("Only Support [com.google.protobuf.MessageLite] Type");
            } catch (Exception e) {
                throw new ConversionException(e);
            }
        }
        return Serializer.super.convert(arg, type);
    }
}
