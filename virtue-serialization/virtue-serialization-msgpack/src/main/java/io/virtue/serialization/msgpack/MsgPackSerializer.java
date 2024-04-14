package io.virtue.serialization.msgpack;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.virtue.common.spi.Extension;
import io.virtue.serialization.json.JacksonSerializer;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import static io.virtue.common.constant.Components.Serialization.MSGPACK;

/**
 * MessagePack Serializer.
 */
@Extension(MSGPACK)
public class MsgPackSerializer extends JacksonSerializer {

    public MsgPackSerializer() {
        super.jsonMapper = JsonMapper.builder(new MessagePackFactory())
                .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                .build();
    }

}
