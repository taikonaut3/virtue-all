package io.github.taikonaut3;

import io.virtue.common.spi.ExtensionLoader;
import io.virtue.core.annotation.Config;
import io.virtue.core.annotation.RemoteService;
import io.virtue.rpc.virtue.config.VirtueCallable;
import io.virtue.serialization.Serializer;
import org.example.model1.ParentObject;

import java.util.List;

import static io.virtue.common.constant.Components.Serialization.JSON;

@RemoteService("345")
public class Provider {

    public static void main(String[] args) {
        Serializer serializer = ExtensionLoader.loadExtension(Serializer.class, "json");
        List<ParentObject> objList = ParentObject.getObjList();
        byte[] bytes = serializer.serialize(objList);

        List deserialize = serializer.deserialize(bytes, List.class);
        System.out.println(deserialize);
    }

    @Config(filters = {"filter2", "filter1"}, serialization = JSON)
    @VirtueCallable(name = "hello")
    public String hello(String world) {
        return "hello" + world;
    }

    @VirtueCallable
    public String world(String world) {
        return "hello" + world;
    }

}
