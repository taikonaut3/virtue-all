package io.virtue.serialization.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.virtue.common.exception.ConversionException;
import io.virtue.common.exception.SerializationException;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.serialization.Serializer;

import java.io.IOException;
import java.lang.reflect.Type;

import static io.virtue.common.constant.Components.Serialization.JSON;

/**
 * Jackson JSON Serializer.
 */
@ServiceProvider(JSON)
public class JacksonSerializer implements Serializer {

    private final ObjectMapper objectMapper;

    public JacksonSerializer() {
        this.objectMapper = JsonMapper.builder().configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true).build();
    }

    @Override
    public byte[] serialize(Object input) throws SerializationException {
        if (input == null) {
            return new byte[0];
        }
        try {
            return objectMapper.writeValueAsBytes(input);
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> type) throws SerializationException {
        if (type == String.class) {
            return (T) new String(bytes);
        }
        try {
            return objectMapper.readValue(bytes, type);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public Object[] convert(Object[] args, Type[] types) throws ConversionException {
        Object[] objects = new Object[types.length];
        try {
            // 遍历 Object[] 数组并匹配相应类型
            for (int i = 0; i < args.length; i++) {
                Object deserializeObj = args[i];
                Type objectType = types[i];
                // 将反序列化的对象转换为指定类型
                Object typedObject = objectMapper.convertValue(deserializeObj, objectMapper.constructType(objectType));
                objects[i] = typedObject;
            }
        } catch (Exception e) {
            throw new ConversionException(e);
        }
        return objects;
    }

    @Override
    public Object convert(Object arg, Type type) throws ConversionException {
        try {
            if (arg.getClass() == String.class) {
                if (type == String.class) {
                    return arg;
                }
                TypeFactory typeFactory = objectMapper.getTypeFactory();
                JavaType javaType = typeFactory.constructType(type);
                return objectMapper.readValue((String) arg, javaType);
            }
            return objectMapper.convertValue(arg, objectMapper.constructType(type));
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }

}
