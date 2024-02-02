package io.github.astro;

import io.github.astro.model2.ParentObject;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.RemoteService;
import io.github.astro.virtue.rpc.virtue.config.VirtueCallable;

import java.util.List;

import static io.github.astro.virtue.common.constant.Components.Serialize.MSGPACK;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/5 18:58
 */
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
