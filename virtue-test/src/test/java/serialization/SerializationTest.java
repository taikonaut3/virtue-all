package serialization;

import io.virtue.metrics.CallerMetrics;
import io.virtue.serialization.json.JacksonSerializer;
import org.junit.jupiter.api.Test;

/**
 * @Author WenBo Zhou
 * @Date 2024/5/31 14:45
 */
public class SerializationTest {

    @Test
    public void jsonTest() {
        JacksonSerializer jacksonSerializer = new JacksonSerializer();
        byte[] bytes = jacksonSerializer.serialize(new CallerMetrics());
        String str = new String(bytes);
        System.out.println(str);
    }
}
