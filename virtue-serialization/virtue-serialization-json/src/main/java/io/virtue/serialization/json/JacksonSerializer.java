package io.virtue.serialization.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.virtue.common.exception.ConversionException;
import io.virtue.common.extension.spi.Extension;
import io.virtue.serialization.AbstractSerializer;
import lombok.Getter;

import java.lang.reflect.Type;

import static io.virtue.common.constant.Components.Serialization.JSON;

/**
 * Jackson JSON Serializer.
 */
@Getter
@Extension(JSON)
public class JacksonSerializer extends AbstractSerializer {

    protected JsonMapper jsonMapper;

    public JacksonSerializer() {
        this.jsonMapper = JsonMapper.builder()
                .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                .visibility(
                        VisibilityChecker.Std.defaultInstance()
                                .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
                                .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
                                .withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                .build();
    }

    @Override
    protected byte[] doSerialize(Object input) throws Exception {
        return jsonMapper.writeValueAsBytes(input);
    }

    @Override
    protected Object doDeserialize(byte[] bytes, Type type) throws Exception {
        if (type == String.class || type == Object.class) {
            return new String(bytes);
        }
        return jsonMapper.readValue(bytes, jsonMapper.constructType(type));
    }

    @Override
    public Object[] convert(Object[] args, Type[] types) throws ConversionException {
        Object[] objects = new Object[types.length];
        try {
            for (int i = 0; i < args.length; i++) {
                Object typedObject = convert(args[i], types[i]);
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
            if (arg instanceof String strArg) {
                if (type == String.class) {
                    return strArg;
                }
                return jsonMapper.readValue(strArg, jsonMapper.constructType(type));
            }
            return jsonMapper.convertValue(arg, jsonMapper.constructType(type));
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }

}
