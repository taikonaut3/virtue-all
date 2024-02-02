package io.github.astro.virtue.boot;

import io.github.astro.virtue.config.manager.Virtue;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties
public class VirtueAutoConfiguration {

    @Bean
    public Virtue virtue() {
        return Virtue.getDefault();
    }

    @Bean
    public VirtueConfigurationProperties virtueConfigurationProperties() {
        return new VirtueConfigurationProperties();
    }

    @Bean
    public SpringBootVirtueStarter springBootvirtueStarter() {
        return new SpringBootVirtueStarter();
    }

}
