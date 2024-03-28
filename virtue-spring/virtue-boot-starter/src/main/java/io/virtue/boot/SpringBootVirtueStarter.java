package io.virtue.boot;

import io.virtue.core.Virtue;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Virtue Stater for Spring Boot.
 */
public class SpringBootVirtueStarter implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Virtue virtue = event.getApplicationContext().getBean(Virtue.class);
        virtue.start();
    }

}
