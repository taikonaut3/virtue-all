package io.github.astro;

import io.github.astro.virtue.boot.EnableVirtue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

}