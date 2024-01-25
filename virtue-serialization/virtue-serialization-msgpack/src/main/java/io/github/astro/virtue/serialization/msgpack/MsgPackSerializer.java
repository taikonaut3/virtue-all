package io.github.astro.virtue.serialization.msgpack;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.astro.virtue.common.exception.SerializationException;
import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.serialization.Serializer;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;

import static io.github.astro.virtue.common.constant.Components.Serialize.MSGPACK;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/25 10:18
 */
@ServiceProvider(MSGPACK)
public class MsgPackSerializer implements Serializer {

    private final ObjectMapper objectMapper;

    public MsgPackSerializer() {
        objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
    }

    @Override
    public byte[] serialize(Object input) throws SerializationException {
        try {
            return objectMapper.writeValueAsBytes(input);
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
        if (clazz == String.class) {
            return (T) new String(bytes);
        }
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
