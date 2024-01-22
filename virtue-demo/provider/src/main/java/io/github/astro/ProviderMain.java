package io.github.astro;

import io.github.astro.virtue.boot.EnableVirtue;
import io.github.astro.virtue.config.config.ServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static io.github.astro.virtue.common.constant.Components.Protocol.HTTP;
import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

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

    @Bean
    public ServerConfig serverConfig() {
        return new ServerConfig(VIRTUE, 2883);
    }

    @Bean
    public ServerConfig httpServerConfig() {
        return new ServerConfig(HTTP, 8086);
    }
}