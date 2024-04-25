package io.github.taikonaut3;

import io.virtue.core.Virtue;
import jakarta.annotation.Resource;
import org.example.MyBean;
import org.example.model1.ParentObject;
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
        consumer.list(ParentObject.getObjList("client list 1"), ParentObject.getObjList("client list 2"));
        return consumer.list(ParentObject.getObjList());
    }

    @GetMapping("http2list")
    public List<ParentObject> http2list() {
        return consumer.http2Test("12", "dasdasdasdsadas", "456", new MyBean(), ParentObject.getObjList());
    }
}
