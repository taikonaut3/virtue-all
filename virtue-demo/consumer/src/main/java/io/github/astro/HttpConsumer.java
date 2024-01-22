package io.github.astro;

import io.github.astro.model.ParentObject;
import io.github.astro.virtue.config.SystemInfo;
import io.github.astro.virtue.config.Virtue;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.Options;
import io.github.astro.virtue.config.annotation.RemoteCaller;
import io.github.astro.virtue.rpc.http1.config.HttpCall;
import io.github.astro.virtue.rpc.http1.config.HttpMethod;
import io.github.astro.virtue.rpc.http1.config.Param;
import io.github.astro.virtue.rpc.http1.config.PathVariable;

import java.util.List;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/9 13:33
 */
@RemoteCaller("provider")
public interface HttpConsumer {

    public static void main(String[] args) {
        SystemInfo systemInfo = Virtue.getDefault().newSystemInfo();
        System.out.println(systemInfo);
    }

    @HttpCall(path = "hello", method = HttpMethod.GET,
            options = @Options(url = "127.0.0.1:8080"))
    String hello(@Param("world") String world);

    @HttpCall(path = "hello/list", method = HttpMethod.POST,
            options = @Options(url = "127.0.0.1:8080"))
    List<ParentObject> list(List<ParentObject> list);

    @HttpCall(path = "hello/list/{id}/name/{name}", method = HttpMethod.POST,
            options = @Options(url = "127.0.0.1:8080"), config = @Config(filters = {"filter1"}))
    String path(@PathVariable("id") String id, @PathVariable("name") String name);
}
