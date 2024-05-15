package io.github.taikonaut3;

import io.github.taikonaut3.filter.CallerResultFilter;
import io.github.taikonaut3.filter.Filter1;
import io.github.taikonaut3.filter.Filter2;
import io.github.taikonaut3.filter.TestFilter;
import io.virtue.boot.EnableVirtue;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.core.Virtue;
import io.virtue.core.config.ApplicationConfig;
import io.virtue.core.config.RegistryConfig;
import io.virtue.core.config.ServerConfig;
import io.virtue.rpc.protocol.Protocol;
import org.example.Message;
import org.example.model1.ParentObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;
import static io.virtue.common.constant.Components.Registry.CONSUL;

@SpringBootApplication
@EnableVirtue(scanBasePackages = "io.github.taikonaut3")
public class ConsumerMain {
    public static void main(String[] args) throws Exception {
//        ConfigurableApplicationContext context = SpringApplication.run(ConsumerMain.class, args);
//        Consumer consumer = context.getBean("consumer", Consumer.class);
//        ExecutorService executorService = Executors.newFixedThreadPool(5);
//        for (int i = 0; i < 10; i++) {
//            executorService.execute(() -> {
//                Message message = new Message();
//                message.setDate(new Date());
//                message.setName("client" + message.getDate().toString());
//                Message message1 = consumer.exchangeMessage(message);
//                System.out.println(message1);
//            });
//
//        }
        //SpringApplication.run(ConsumerMain.class, args);
        simpleTest();
    }

    public static void simpleTest() throws Exception {
        Virtue virtue = Virtue.getDefault();
        virtue.wrap(new Provider());
        Consumer consumer = virtue.application(new ApplicationConfig("consumer"))
                .register(new RegistryConfig("consul://127.0.0.1:8500"))
                .register("filter1", new Filter1())
                .register("filter2", new Filter2())
                .register("testFilter", new TestFilter(virtue))
                .register("callerResultFilter", new CallerResultFilter(virtue))
                .router("^virtue://.*/345/list", ":2333")
                //.register(new RegistryConfig("nacos://127.0.0.1:8848"))
                .proxy(Consumer.class)
                .remoteCaller(Consumer.class)
                .get();
        virtue.start();

        Protocol protocol = ExtensionLoader.loadExtension(Protocol.class, "h2");

        //List<ParentObject> httplist = consumer.http2Test(ParentObject.getObjList("client list"));
        Message message = new Message();
        message.setDate(new Date());
        message.setName("client" + message.getDate().toString());
        Message message1 = consumer.exchangeMessage(message);
        System.out.println(message1);
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        for (int i = 0; i < 200; i++) {
            executorService.execute(() -> {
                long start = System.currentTimeMillis();
                List<ParentObject> list = consumer.list(ParentObject.getObjList("client list 1"), ParentObject.getObjList("client list 2"));
                long end = System.currentTimeMillis();
                System.out.println("virtue 耗时:" + (end - start) + list);
                start = System.currentTimeMillis();
                //List<ParentObject> clientList = consumer.http2Test(ParentObject.getObjList("client list"));
                end = System.currentTimeMillis();
                //System.out.println("h2 耗时:" + (end - start) + clientList);
            });
        }
        String hello = consumer.hello("world");
        String hello1 = consumer.hello("world2");
        System.out.println(hello + hello1);
        CompletableFuture<String> future = consumer.helloAsync("worldAsync");
        String s = null;
        try {
            s = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println(s);
        System.in.read();
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