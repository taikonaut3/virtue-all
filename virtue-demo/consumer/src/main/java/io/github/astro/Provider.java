package io.github.astro;

import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.RemoteService;
import io.github.astro.virtue.rpc.virtue.config.VirtueCallable;

import static io.github.astro.virtue.common.constant.Components.Serialize.JSON;

@RemoteService("345")
public class Provider {

    @Config(filters = {"filter2", "filter1"}, serialize = JSON)
    @VirtueCallable(name = "hello")
    public String hello(String world) {
        return "hello" + world;
    }

    @VirtueCallable
    public String world(String world) {
        return "hello" + world;
    }

}
