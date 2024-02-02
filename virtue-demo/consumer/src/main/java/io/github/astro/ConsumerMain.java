package io.github.astro;

import io.github.astro.model.ParentObject;
import io.github.astro.virtue.boot.EnableVirtue;
import io.github.astro.virtue.config.config.RegistryConfig;
import io.github.astro.virtue.config.config.ServerConfig;
import io.github.astro.virtue.config.manager.Virtue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;
import static io.github.astro.virtue.common.constant.Components.Registry.CONSUL;

/**
 * ${END}
 *
 * @Author WenBo Zhou
 * @Date 2024/1/7 19:53
 */
@SpringBootApplication
@EnableVirtue(scanBasePackages = "io.github.astro")
public class ConsumerMain {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerMain.class, args);
    }

    public static void simpleTest() {
        Virtue virtue = Virtue.getDefault();
        Consumer consumer = virtue.applicationName("consumer").register(new RegistryConfig("consul://127.0.0.1:8500")).register(new RegistryConfig("nacos://127.0.0.1:8848")).proxy(Consumer.class).remoteCaller(Consumer.class).get();
        for (int i = 0; i < 50; i++) {
            long start = System.currentTimeMillis();
            List<ParentObject> list = consumer.list(ParentObject.getObjList());
            long end = System.currentTimeMillis();
            System.out.println("耗时:" + (end - start) + list);
        }
    }

    @Bean
    public ServerConfig serverConfig() {
        return new ServerConfig(VIRTUE, 2999);
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
    public RegistryConfig consulRegistryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.type(CONSUL);
        registryConfig.host("127.0.0.1");
        registryConfig.port(8500);
        return registryConfig;
    }
}