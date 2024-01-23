package io.github.astro.virtue.rpc.virtue;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.RemoteService;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.config.ServerConfig;
import io.github.astro.virtue.config.manager.ServerConfigManager;
import io.github.astro.virtue.config.util.GenerateUtil;
import io.github.astro.virtue.rpc.config.AbstractServerCaller;
import io.github.astro.virtue.rpc.virtue.config.VirtueCallable;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;
import static io.github.astro.virtue.common.constant.Constant.DEFAULT_PROTOCOL_PORT;

@ToString
public class VirtueServerCaller extends AbstractServerCaller<VirtueCallable> {

    private static final Logger logger = LoggerFactory.getLogger(VirtueServerCaller.class);

    private String remoteService;

    private String callMethod;

    public VirtueServerCaller(Method method, RemoteService<?> remoteService) {
        super(method, remoteService, VIRTUE, VirtueCallable.class);
    }

    @Override
    public void doInit() {
        this.remoteService = remoteService().name();
        this.callMethod = StringUtil.isBlank(parsedAnnotation.name()) ? GenerateUtil.generateKey(method()) : parsedAnnotation.name();
    }

    @Override
    protected Config config() {
        return parsedAnnotation.config();
    }

    @Override
    protected URL createUrl() {
        ServerConfigManager serverConfigManager = virtue.configManager().serverConfigManager();
        ServerConfig serverConfig = serverConfigManager.get(protocol);
        URL url = serverConfig.toUrl();
        url.addPath(remoteService);
        url.addPath(callMethod);
        url.addParameter(Key.CLASS, remoteService().target().getClass().getName());
        url.addParameters(parameterization());
        return url;
    }

    @Override
    public String path() {
        return "/" + remoteService + "/" + callMethod;
    }

    @Override
    protected ServerConfig defaultServerConfig() {
        return new ServerConfig(VIRTUE, DEFAULT_PROTOCOL_PORT);
    }
}
