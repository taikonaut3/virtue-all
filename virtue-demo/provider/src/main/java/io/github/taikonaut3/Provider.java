package io.github.taikonaut3;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.virtue.core.annotation.Config;
import io.virtue.core.annotation.RemoteService;
import io.virtue.rpc.h2.config.Body;
import io.virtue.rpc.h2.config.Http2Callable;
import io.virtue.rpc.virtue.config.VirtueCallable;
import org.example.Message;
import org.example.model2.ParentObject;

import java.util.Date;
import java.util.List;

import static io.virtue.common.constant.Components.Serialization.MSGPACK;

@RemoteService("345")
//@Service
public class Provider {

    @VirtueCallable(name = "hello")
    public String hello(String world) {
        return "hello" + world;
    }

    @VirtueCallable
    public String world(String world) {
        return "hello" + world;
    }

    //@HttpCallable
    public String httpHello() {
        return "hello";
    }

    @VirtueCallable(name = "list")
    @Config(serialization = MSGPACK, filters = "testFilter")
    public List<ParentObject> list(List<ParentObject> list) {
        return ParentObject.getObjList();
    }

    @Config(serialization = MSGPACK)
    @VirtueCallable(name = "list2")
    public List<ParentObject> list2(List<ParentObject> list1, List<ParentObject> list2) {
        return ParentObject.getObjList("list server2");
    }

    @Bulkhead(name = "bulkheadApi")
    @VirtueCallable(name = "exchangeMessage")
    @Config(filters = {"testFilter", "calleeResultFilter"})
    public Message exchangeMessage(Message message) {
        message.setDate(new Date());
        message.setName("server " + message.getDate().toString());
//        try {
//            Thread.sleep(6000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return message;
    }

    @Http2Callable(path = "http2Test")
    public List<ParentObject> http2Test(@Body List<ParentObject> list) {
        return ParentObject.getObjList("list server2");
    }

}
