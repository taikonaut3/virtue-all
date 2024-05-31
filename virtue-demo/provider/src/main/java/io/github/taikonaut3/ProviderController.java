package io.github.taikonaut3;

import io.virtue.common.util.GenerateUtil;
import io.virtue.core.Invoker;
import io.virtue.core.Virtue;
import io.virtue.metrics.CalleeMetrics;
import jakarta.annotation.Resource;
import org.example.model2.ParentObject;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProviderController {

    private String name;

    @Resource
    private Virtue virtue;

    @GetMapping("hello")
    public String hello(@RequestParam("world") String world) {
        return "hello" + world;
    }

    @PostMapping("hello/list")
    public List<ParentObject> list(@RequestBody List<ParentObject> list) {
        return ParentObject.getObjList();
    }

    @PostMapping("hello/list/{id}/name/{name1}")
    public String path(@PathVariable("id") String id, @PathVariable("name1") String name) {
        return id + name;
    }

    @GetMapping("calleeMetrics")
    public Map<String, String> calleeMetrics() {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        virtue.configManager().remoteServiceManager().remoteServices().forEach(remoteService -> {
            for (Invoker<?> invoker : remoteService.invokers()) {
                CalleeMetrics calleeMetrics = invoker.get(CalleeMetrics.ATTRIBUTE_KEY);
                result.put(GenerateUtil.generateCalleeMapping(invoker.protocol(), invoker.path()), calleeMetrics.toString());
            }
        });
        return result;
    }
}
