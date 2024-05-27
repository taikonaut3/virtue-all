package io.github.taikonaut3;

import io.virtue.boot.EnableVirtue;
import io.virtue.core.Invoker;
import io.virtue.core.Virtue;
import io.virtue.core.config.ApplicationConfig;
import io.virtue.core.config.RegistryConfig;
import io.virtue.core.config.ServerConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;
import static io.virtue.common.constant.Components.Registry.CONSUL;

@SpringBootApplication
@EnableVirtue(scanBasePackages = "io.github.taikonaut3")
public class ProviderMain {
    public static void main(String[] args) throws Exception {
        //SpringApplication.run(ProviderMain.class, args);
        simpleTest();
    }

    public static void simpleTest() {
        Virtue virtue = Virtue.getDefault();
        virtue.application(new ApplicationConfig("provider"))
                .register(new ServerConfig(VIRTUE, 2333))
                .register(new ServerConfig("h2c", 8082))
                .register(new ServerConfig("h2", 8083))
                .register(new ServerConfig("http", 8085))
                .register(new ServerConfig("https", 8086))
                .register(new RegistryConfig("consul://127.0.0.1:8500"))
                .wrap(new Provider())
                .start();
        virtue.configManager().remoteServiceManager().remoteServices().forEach(remoteService -> {
            for (Invoker<?> invoker : remoteService.invokers()) {
                invoker.addFilter();
            }
        });
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
    public ServerConfig h2ServerConfig() {
        return new ServerConfig("h2c", 8082);
    }

    @Bean
    public ServerConfig h2cServerConfig() {
        return new ServerConfig("h2", 8083);
    }

    @Bean
    public ServerConfig httpServerConfig() {
        return new ServerConfig("http", 8085);
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