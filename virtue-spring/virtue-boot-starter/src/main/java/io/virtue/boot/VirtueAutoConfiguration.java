package io.virtue.boot;

import io.virtue.core.Virtue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Virtue auto configuration.
 */
@EnableConfigurationProperties
public class VirtueAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Virtue.class)
    public Virtue virtue() {
        return Virtue.getDefault();
    }

    @Bean
    public VirtueConfigurationProperties virtueConfigurationProperties() {
        return new VirtueConfigurationProperties();
    }

    @Bean
    public SpringBootVirtueStarter springBootVirtueStarter() {
        return new SpringBootVirtueStarter();
    }

}
