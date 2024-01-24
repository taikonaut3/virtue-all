package io.github.astro.virtue.boot;

import io.github.astro.virtue.config.Virtue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
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

    @Bean
    @ConditionalOnClass(DiscoveryClient.class)
    public ServiceRegistrationPostProcessor serviceRegistrationPostProcessor() {
        return new ServiceRegistrationPostProcessor();
    }

}
