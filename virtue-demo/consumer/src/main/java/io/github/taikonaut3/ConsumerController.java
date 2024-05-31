package io.github.taikonaut3;

import io.github.taikonaut3.filter.CallerMetricsExportFilter;
import io.virtue.common.util.GenerateUtil;
import io.virtue.core.Invoker;
import io.virtue.core.Virtue;
import io.virtue.metrics.CallerMetrics;
import jakarta.annotation.Resource;
import org.example.model1.ParentObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class ConsumerController {

    @Resource
    Consumer consumer;

    @Resource
    Virtue virtue;

    @GetMapping("list")
    public List<ParentObject> list() {
        consumer.list(ParentObject.getObjList("client list 1"), ParentObject.getObjList("client list 2"));
        return consumer.list(ParentObject.getObjList());
    }

    @GetMapping("http2list")
    public List<ParentObject> http2list() {
        //List<ParentObject> list = consumer.http2Test(ParentObject.getObjList());
        return consumer.http2Test(ParentObject.getObjList());
    }

    @GetMapping("httplist")
    public List<ParentObject> httplist() {
        //List<ParentObject> list = consumer.http2Test(ParentObject.getObjList());
        return consumer.httpTest(ParentObject.getObjList());
    }

    @GetMapping("testTime")
    public Map<String, String> testTime() {
        ExecutorService executorService = Executors.newFixedThreadPool(3000);
        ArrayList<CompletableFuture<?>> futures = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            var future = CompletableFuture.supplyAsync(() -> consumer.list(ParentObject.getObjList()), executorService);
            futures.add(future);
        }
        for (int i = 0; i < 1000; i++) {
            var future = CompletableFuture.supplyAsync(() -> consumer.http2Test(ParentObject.getObjList()), executorService);
            futures.add(future);
        }
        for (int i = 0; i < 1000; i++) {
            var future = CompletableFuture.supplyAsync(() -> consumer.httpTest(ParentObject.getObjList()), executorService);
            futures.add(future);
        }
        try {
            CompletableFuture<Void> future = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Map.of(
                "virtue", CallerMetricsExportFilter.virtueWrapper.toString(),
                "h2", CallerMetricsExportFilter.h2Wrapper.toString(),
                "http", CallerMetricsExportFilter.httpWrapper.toString()
        );
    }

    @GetMapping("callerMetrics")
    public Map<String, String> callerMetrics() {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        virtue.configManager().remoteCallerManager().remoteCallers().forEach(remoteCaller -> {
            for (Invoker<?> invoker : remoteCaller.invokers()) {
                CallerMetrics callerMetrics = invoker.get(CallerMetrics.ATTRIBUTE_KEY);
                result.put(GenerateUtil.generateCalleeMapping(invoker.protocol(), invoker.path()), callerMetrics.toString());
            }
        });
        return result;
    }
}
