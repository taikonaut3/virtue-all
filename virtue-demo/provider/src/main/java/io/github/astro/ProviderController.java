package io.github.astro;

import io.github.astro.model2.ParentObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/9 16:11
 */

@RestController
public class ProviderController {

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
}
