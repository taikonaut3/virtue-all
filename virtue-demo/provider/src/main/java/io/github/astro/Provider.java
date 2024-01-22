package io.github.astro;

import io.github.astro.rpc.virtue.config.VirtueCallable;
import io.github.astro.virtue.config.annotation.RemoteService;
import io.github.astro.virtue.rpc.http1.config.HttpCallable;

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

    @HttpCallable
    public String httpHello() {
        return "hello";
    }
}
