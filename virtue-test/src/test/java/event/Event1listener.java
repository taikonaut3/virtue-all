package event;

import io.virtue.event.EventListener;

/**
 * @Author WenBo Zhou
 * @Date 2024/4/18 16:14
 */
public class Event1listener implements EventListener<Event1> {
    @Override
    public void onEvent(Event1 event) {

        System.out.println(Thread.currentThread().threadId() + "接收到1" + event.source());
    }
}
