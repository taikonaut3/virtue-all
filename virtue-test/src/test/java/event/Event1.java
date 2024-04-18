package event;

import io.virtue.event.AbstractEvent;

/**
 * @Author WenBo Zhou
 * @Date 2024/4/18 16:14
 */
public class Event1 extends AbstractEvent<String> {


    public Event1(String data) {
        super(data);
    }
}
