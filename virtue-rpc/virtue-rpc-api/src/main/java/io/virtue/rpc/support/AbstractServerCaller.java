package io.virtue.rpc.support;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.core.CallArgs;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteService;
import io.virtue.core.ServerCaller;
import io.virtue.core.config.ServerConfig;
import io.virtue.core.filter.Filter;
import io.virtue.core.filter.FilterScope;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


@Getter
@Accessors(fluent = true)
public abstract class AbstractServerCaller<T extends Annotation> extends AbstractCaller<T> implements ServerCaller<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServerCaller.class);

    protected String desc;

    protected AbstractServerCaller(Method method, RemoteService<?> remoteService, String protocol, Class<T> annoType) {
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
    public Object call(URL url, CallArgs args) throws RpcException {
        List<Filter> preFilters = FilterScope.PRE.filterScope(filters);
        Invocation invocation = Invocation.create(url, args, () -> {
            method.setAccessible(true);
            try {
                return method.invoke(remoteService().target(), args.args());
            } catch (IllegalAccessException e) {
                throw new RpcException(e);
            } catch (InvocationTargetException e) {
                throw new RpcException(e.getTargetException());
            }
        });
        Object result = filterChain.filter(invocation, preFilters);
        url.addParameters(this.url.parameters());
        return protocolInstance.createResponse(url, result);

    }

    @Override
    protected URL createUrl(URL serverUrl) {
        serverUrl.replacePaths(pathList());
        serverUrl.addParameters(parameterization());
        serverUrl.addParameter(Key.CLASS, remoteService().target().getClass().getName());
        return serverUrl;
    }

    @Override
    public RemoteService<?> remoteService() {
        return (RemoteService<?>) container();
    }

}
