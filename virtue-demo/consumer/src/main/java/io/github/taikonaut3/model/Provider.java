package io.github.taikonaut3.model;

import io.virtue.config.annotation.Config;
import io.virtue.config.annotation.RemoteService;
import io.virtue.rpc.virtue.config.VirtueCallable;

import java.util.List;

import static io.virtue.common.constant.Components.Serialize.MSGPACK;

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

    @VirtueCallable(name = "list", config = @Config(serialize = MSGPACK))
    public List<ParentObject> list(List<ParentObject> list) {
        return ParentObject.getObjList();
    }

}
