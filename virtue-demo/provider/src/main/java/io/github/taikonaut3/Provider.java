package io.github.taikonaut3;

import io.github.taikonaut3.model2.ParentObject;
import io.virtue.core.annotation.Config;
import io.virtue.core.annotation.RemoteService;
import io.virtue.rpc.virtue.config.VirtueCallable;
import org.example.Message;

import java.util.Date;
import java.util.List;

import static io.virtue.common.constant.Components.Serialization.MSGPACK;

@RemoteService("345")
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

    @VirtueCallable(name = "list", config = @Config(serialization = MSGPACK,filters = "testFilter"))
    public List<ParentObject> list(List<ParentObject> list) {
        return ParentObject.getObjList();
    }

    @Config(serialization = MSGPACK)
    @VirtueCallable(name = "list2")
    public List<ParentObject> list2(List<ParentObject> list1, List<ParentObject> list2) {
        return ParentObject.getObjList("list server2");
    }

    @VirtueCallable(name = "exchangeMessage")
    @Config(filters = {"testFilter","calleeResultFilter"})
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

}
