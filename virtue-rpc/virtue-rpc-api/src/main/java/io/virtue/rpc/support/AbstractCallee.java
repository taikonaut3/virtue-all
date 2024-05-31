package io.virtue.rpc.support;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.common.util.DateUtil;
import io.virtue.core.Callee;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteService;
import io.virtue.core.config.ServerConfig;
import io.virtue.core.filter.Filter;
import io.virtue.core.filter.FilterScope;
import io.virtue.metrics.CalleeMetrics;
import io.virtue.transport.channel.Channel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
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
            if (logger.isWarnEnabled()) {
                logger.warn("Can't find <{}>ServerConfig", protocol);
            }
        }
        addInitData();
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        URL url = invocation.url();
        boolean oneway = url.getBooleanParam(Key.ONEWAY);
        long timeout = url.getLongParam(Key.TIMEOUT);
        String timestamp = url.getParam(Key.TIMESTAMP);
        LocalDateTime localDateTime = DateUtil.parse(timestamp, DateUtil.COMPACT_FORMAT);
        Object result = null;
        if (oneway) {
            result = doInvoke(invocation);
        } else {
            long margin = Duration.between(localDateTime, LocalDateTime.now()).toMillis();
            if (margin < timeout) {
                result = doInvoke(invocation);
                long invokeAfterMargin = Duration.between(localDateTime, LocalDateTime.now()).toMillis();
                if (invokeAfterMargin < timeout) {
                    sendResponse(invocation, result);
                }
            }
        }
        if (result instanceof Exception e) {
            throw RpcException.unwrap(e);
        }
        return result;
    }

    @Override
    public RemoteService<?> remoteService() {
        return (RemoteService<?>) container();
    }

    @Override
    protected URL createUrl(URL serverUrl) {
        serverUrl.replacePaths(pathList());
        serverUrl.addParams(parameterization());
        serverUrl.addParam(Key.CLASS, remoteService().target().getClass().getName());
        return serverUrl;
    }

    protected void addInitData() {
        set(CalleeMetrics.ATTRIBUTE_KEY, new CalleeMetrics());
    }

    protected Object doInvoke(Invocation invocation) {
        List<Filter> preFilters = FilterScope.PRE.filterScope(filters);
        URL url = invocation.url();
        invocation.revise(() -> {
            Object result;
            try {
                result = remoteService().invokeMethod(method, invocation.args());
            } catch (Exception e) {
                logger.error("Invoke " + url.path() + " failed", e);
                result = e;
            }
            return result;
        });
        return filterChain.filter(invocation, preFilters);
    }

    protected void sendResponse(Invocation invocation, Object result) {
        URL url = invocation.url();
        Channel channel = url.get(Channel.ATTRIBUTE_KEY);
        url.addParams(this.url.params());
        List<Filter> postFilters = FilterScope.POST.filterScope(filters);
        invocation.revise(() -> {
            protocolInstance.sendResponse(channel, invocation, result);
            return null;
        });
        filterChain.filter(invocation, postFilters);
    }
}
