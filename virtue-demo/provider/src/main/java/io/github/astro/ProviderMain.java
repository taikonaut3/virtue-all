package io.github.astro;

import io.github.astro.virtue.boot.EnableVirtue;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.config.config.RegistryConfig;
import io.github.astro.virtue.config.config.ServerConfig;
import io.github.astro.virtue.config.manager.Virtue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;
import static io.github.astro.virtue.common.constant.Components.Registry.CONSUL;

/**
 * ${END}
 *
 * @Author WenBo Zhou
 * @Date 2024/1/7 19:52
 */
@SpringBootApplication
@EnableVirtue(scanBasePackages = "io.github.astro")
public class ProviderMain {
    public static void main(String[] args) {
        SpringApplication.run(ProviderMain.class, args);
    }

    public static void simpleTest() {
        Virtue virtue = Virtue.getDefault();
        virtue.applicationName("provider")
                .register(new ServerConfig(Key.VIRTUE, 2888))
                .register(new RegistryConfig("consul://127.0.0.1:8500"))
                .register(new RegistryConfig("nacos://127.0.0.1:8848"))
                .wrap(new Provider())
                .start();
    }
//    @Bean
//    public RegistryConfig nacosRegistryConfig() {
//        RegistryConfig registryConfig = new RegistryConfig();
//        registryConfig.type(NACOS);
//        registryConfig.host("127.0.0.1");
//        registryConfig.port(8848);
//        return registryConfig;
//    }

    @Bean
    public ServerConfig serverConfig() {
        return new ServerConfig(VIRTUE, 2333);
    }

    @Bean
    public RegistryConfig consulRegistryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.type(CONSUL);
        registryConfig.host("127.0.0.1");
        registryConfig.port(8500);
        return registryConfig;
    }

}