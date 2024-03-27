package io.github.taikonaut3;

import io.virtue.core.annotation.Config;
import io.virtue.core.annotation.RemoteService;
import io.virtue.rpc.virtue.config.VirtueCallable;

import static io.virtue.common.constant.Components.Serialization.JSON;

@RemoteService("345")
public class Provider {

    @Config(filters = {"filter2", "filter1"}, serialization = JSON)
    @VirtueCallable(name = "hello")
    public  String hello(String world) {
        return "hello" + world;
    }

    @VirtueCallable
    public String world(String world) {
        return "hello" + world;
    }

}
