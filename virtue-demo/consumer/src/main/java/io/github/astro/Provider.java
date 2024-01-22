package io.github.astro;

import io.github.astro.virtue.config.annotation.RemoteService;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/5 18:58
 */
@RemoteService("345")
public class Provider {

    @io.github.astro.rpc.virtue.config.VirtueCallable(name = "hello")
    public String hello(String world) {
        return "hello" + world;
    }

    @io.github.astro.rpc.virtue.config.VirtueCallable
    public String world(String world) {
        return "hello" + world;
    }
}
