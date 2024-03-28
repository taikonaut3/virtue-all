package io.github.taikonaut3;

import io.github.taikonaut3.model.ParentObject;
import io.virtue.core.Virtue;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConsumerController {

    @Resource
    Consumer consumer;


    @Resource
    Virtue virtue;


    @GetMapping("list")
    public List<ParentObject> list() {
        return consumer.list(ParentObject.getObjList());
    }
}
