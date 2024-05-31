package io.virtue.core.aot;

import io.virtue.common.aot.AotCompiler;
import io.virtue.common.aot.ReflectMeta;
import io.virtue.common.extension.spi.Extension;
import io.virtue.core.config.*;

/**
 * @Author WenBo Zhou
 * @Date 2024/5/29 16:37
 */
@Extension
public class CoreAotCompiler implements AotCompiler {
    @Override
    public void process() {
        register(new ReflectMeta(ApplicationConfig.class).allDeclaredFields(true));
        register(new ReflectMeta(ClientConfig.class).allDeclaredFields(true));
        register(new ReflectMeta(EventDispatcherConfig.class).allDeclaredFields(true));
        register(new ReflectMeta(RegistryConfig.class).allDeclaredFields(true));
        register(new ReflectMeta(ApplicationConfig.class).allDeclaredFields(true));
        register(new ReflectMeta(ServerConfig.class).allDeclaredFields(true));
    }
}
