package io.github.astro.virtue.rpc.config;

import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.exception.SourceException;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.config.RemoteService;
import io.github.astro.virtue.config.ServerCaller;
import io.github.astro.virtue.config.config.RegistryConfig;
import io.github.astro.virtue.config.config.ServerConfig;
import io.github.astro.virtue.config.manager.ServerConfigManager;
import io.github.astro.virtue.registry.Registry;
import io.github.astro.virtue.registry.RegistryFactory;
import io.github.astro.virtue.rpc.ComplexServerInvoker;
import io.github.astro.virtue.rpc.protocol.Protocol;
import io.github.astro.virtue.transport.server.Server;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/5 16:31
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractServerCaller<T extends Annotation> extends AbstractCaller<T> implements ServerCaller<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServerCaller.class);

    public static final Map<String, Server> hadExportServer = new LinkedHashMap<>();

    protected String desc;

    protected AbstractServerCaller(Method method, RemoteService<?> remoteService, String protocol, Class<T> annoType) {
        super(method, remoteService, protocol, annoType);
    }

    @Override
    public void init() {
        doInit();
    }

    @Override
    public Object call(CallArgs args) throws RpcException {
        method.setAccessible(true);
        try {
            return method.invoke(remoteService().target(), args.args());
        } catch (IllegalAccessException e) {
            throw new RpcException(e);
        } catch (InvocationTargetException e) {
            throw new RpcException(e.getTargetException());
        }
    }

    @Override
    public void doStart() {
        checkServerConfig();
        url = createUrl();
        // 检查当前协议是否已经开启
        String authority = url.authority();
        if (!hadExportServer.containsKey(authority)) {
            for (RegistryConfig registryConfig : registryConfigs()) {
                URL registryConfigUrl = registryConfig.toUrl();
                RegistryFactory registryFactory = ExtensionLoader.loadService(RegistryFactory.class, registryConfigUrl.protocol());
                Registry registry = registryFactory.get(registryConfigUrl);
                registry.register(url);
            }

            // 开启协议端口
            Protocol<?,?> protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
            Server server = protocol.openServer(url);
            hadExportServer.put(authority, server);
        }
        invoker = new ComplexServerInvoker(this);
    }

    @Override
    public RemoteService<?> remoteService() {
        return (RemoteService<?>) container();
    }

    private void checkServerConfig() {
        ServerConfigManager serverConfigManager = virtue.configManager().serverConfigManager();
        ServerConfig serverConfig = serverConfigManager.get(protocol);
        if (serverConfig == null) {
            serverConfig = defaultServerConfig();
            if (serverConfig == null) {
                throw new SourceException("Unknown found " + protocol + "'s serverConfig");
            } else {
                serverConfigManager.register(serverConfig);
            }
        }
    }

    protected abstract ServerConfig defaultServerConfig();

}
