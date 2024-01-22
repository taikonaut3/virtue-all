package io.github.astro.virtue.boot;

import io.github.astro.virtue.config.Virtue;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 启动 virtue
 */
public class SpringBootVirtueStarter implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Virtue virtueApplication = event.getApplicationContext().getBean(Virtue.class);
        virtueApplication.start();
    }

}
