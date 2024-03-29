package io.github.taikonaut3;

import io.github.taikonaut3.filter.CallerResultFilter;
import io.github.taikonaut3.filter.Filter1;
import io.github.taikonaut3.filter.Filter2;
import io.github.taikonaut3.filter.TestFilter;
import io.github.taikonaut3.model.ParentObject;
import io.virtue.boot.EnableVirtue;
import io.virtue.core.MatchRule;
import io.virtue.core.Virtue;
import io.virtue.core.config.ApplicationConfig;
import io.virtue.core.config.RegistryConfig;
import io.virtue.core.config.ServerConfig;
import org.example.Message;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;
import static io.virtue.common.constant.Components.Registry.CONSUL;

@SpringBootApplication
@EnableVirtue(scanBasePackages = "io.github.taikonaut3")
public class ConsumerMain {
    public static void main(String[] args) {
        //SpringApplication.run(ConsumerMain.class, args);
        simpleTest();
    }

    public static void simpleTest() {
        Virtue virtue = Virtue.getDefault();
        virtue.wrap(new Provider());
        Consumer consumer = virtue.application(new ApplicationConfig("consumer"))
                .register(new RegistryConfig("consul://127.0.0.1:8500"))
                .register("filter1", new Filter1().addProtocolRule(virtue, MatchRule.Scope.client,".*"))
                .register("filter2",new Filter2())
                .register("testFilter",new TestFilter())
                .register("callerResultFilter", new CallerResultFilter())
                .router("^virtue://.*/345/list",":2333")
                //.register(new RegistryConfig("nacos://127.0.0.1:8848"))
                .proxy(Consumer.class)
                .remoteCaller(Consumer.class)
                .get();
        virtue.start();
        Message message = new Message();
        message.setDate(new Date());
        message.setName("client" + message.getDate().toString());
        Message message1 = consumer.exchangeMessage(message);
        System.out.println(message1);
        for (int i = 0; i < 50; i++) {
            long start = System.currentTimeMillis();
            List<ParentObject> list = consumer.list(ParentObject.getObjList("client list 1"), ParentObject.getObjList("client list 2"));
            long end = System.currentTimeMillis();
            System.out.println("耗时:" + (end - start) + list);
        }
//        String hello = consumer.hello("world");
//        String hello1 = consumer.hello("world2");
//        System.out.println(hello+hello1);
//        CompletableFuture<String> future = consumer.helloAsync("worldAsync");
//        String s = null;
//        try {
//            s = future.get();
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(s);
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