package io.virtue.rpc.support;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteService;
import io.virtue.core.config.ServerConfig;
import io.virtue.core.filter.Filter;
import io.virtue.core.filter.FilterScope;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Abstract Server Callee.
 *
 * @param <T>
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractCallee<T extends Annotation> extends AbstractInvoker<T> implements Callee<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCallee.class);

    protected String desc;

    protected AbstractCallee(Method method, RemoteService<?> remoteService, String protocol, Class<T> annoType) {
        super(method, remoteService, protocol, annoType);
    }

    @Override
    public void init() {
        doInit();
        ServerConfig serverConfig = virtue.configManager().serverConfigManager().get(protocol);
        if (serverConfig != null) {
            url = createUrl(serverConfig.toUrl());
        } else {
            logger.warn("Can't Find [{}]protocol's ServerConfig", protocol);
        }
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        URL url = invocation.url();
        List<Filter> preFilters = FilterScope.PRE.filterScope(filters);
        invocation.revise(() -> {
            try {
                return remoteService().invokeMethod(method, invocation.args());
            } catch (Exception e) {
                throw RpcException.unwrap(e);
            }
        });
        Object result = filterChain.filter(invocation, preFilters);
        url.addParams(this.url.parameters());
        return protocolInstance.createResponse(url, result);

    }

    @Override
    protected URL createUrl(URL serverUrl) {
        serverUrl.replacePaths(pathList());
        serverUrl.addParams(parameterization());
        serverUrl.addParam(Key.CLASS, remoteService().target().getClass().getName());
        return serverUrl;
    }

    @Override
    public RemoteService<?> remoteService() {
        return (RemoteService<?>) container();
    }

}
