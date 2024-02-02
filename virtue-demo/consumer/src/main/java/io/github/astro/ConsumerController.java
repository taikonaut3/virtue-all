package io.github.astro;

import io.github.astro.model.ParentObject;
import io.github.astro.virtue.config.manager.Virtue;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/16 19:02
 */

@RestController
public class ConsumerController {

    @Resource
    Consumer consumer;

    @Resource
    HttpConsumer httpConsumer;

    @Resource
    Virtue virtue;

    @GetMapping("hello/{world}")
    public String hello(@PathVariable("world") String world) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = consumer.helloAsync(world);
        String hello = future.get();
        String hello1 = httpConsumer.hello(world);
        String path = httpConsumer.path("22222", "6546546");
        CompletableFuture<List<ParentObject>> list = httpConsumer.list(ParentObject.getObjList());
        List<ParentObject> parentObjects = list.get();
        return hello + " " + hello1 + " " + path;
    }

    @GetMapping("list")
    public List<ParentObject> list() {
        return consumer.list(ParentObject.getObjList());
    }
}
