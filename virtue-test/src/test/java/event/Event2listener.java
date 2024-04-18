package event;

import io.virtue.event.EventListener;

/**
 * @Author WenBo Zhou
 * @Date 2024/4/18 16:14
 */
public class Event2listener implements EventListener<Event2> {
    @Override
    public void onEvent(Event2 event) {

        System.out.println(Thread.currentThread().threadId() + "接收到2" + event.source());
    }
}
