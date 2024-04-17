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
import io.virtue.rpc.event.SendMessageEvent;
import io.virtue.rpc.protocol.Protocol;
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
public abstract class AbstractCallee<T extends Annotation, P extends Protocol<?, ?>> extends AbstractInvoker<T, P> implements Callee<T> {

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
        boolean oneway = url.getBooleanParam(Key.ONEWAY);
        long timeout = url.getLongParam(Key.TIMEOUT);
        String timestamp = url.getParam(Key.TIMESTAMP);
        LocalDateTime localDateTime = DateUtil.parse(timestamp, DateUtil.COMPACT_FORMAT);
        Object result = null;
        try {
            if (oneway) {
                result = doInvoke(invocation);
            } else {
                long margin = Duration.between(localDateTime, LocalDateTime.now()).toMillis();
                if (margin < timeout) {
                    result = doInvoke(invocation);
                    long invokeAfterMargin = Duration.between(localDateTime, LocalDateTime.now()).toMillis();
                    if (invokeAfterMargin < timeout) {
                        sendSuccessMessageEvent(invocation, result);
                    }
                }
            }
        } catch (RpcException e) {
            long invokeAfterMargin = Duration.between(localDateTime, LocalDateTime.now()).toMillis();
            if (!oneway && invokeAfterMargin < timeout) {
                sendErrorMessageEvent(invocation, e);
            }
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

    protected Object doInvoke(Invocation invocation) throws RpcException {
        List<Filter> preFilters = FilterScope.PRE.filterScope(filters);
        List<Filter> postFilters = FilterScope.POST.filterScope(filters);
        URL url = invocation.url();
        invocation.revise(() -> {
            Object result;
            Object response = null;
            try {
                result = remoteService().invokeMethod(method, invocation.args());
                response = protocolInstance.createResponse(invocation, result);
                return result;
            } catch (Exception e) {
                logger.error("Invoke " + url.path() + " fail", e);
                response = protocolInstance.createResponse(invocation.url(), e);
                throw RpcException.unwrap(e);
            } finally {
                if (response != null) {
                    url.set(Key.SERVICE_RESPONSE, response);
                }
                invocation.revise(null);
                filterChain.filter(invocation, postFilters);
            }
        });
        return filterChain.filter(invocation, preFilters);
    }

    protected void sendSuccessMessageEvent(Invocation invocation, Object result) {
        URL url = invocation.url();
        Channel channel = url.get(Channel.ATTRIBUTE_KEY);
        url.addParams(this.url.params());
        virtue.eventDispatcher().dispatch(new SendMessageEvent(() -> sendSuccess(invocation, channel, result)));
    }

    protected void sendErrorMessageEvent(Invocation invocation, Throwable cause) {
        URL url = invocation.url();
        Channel channel = url.get(Channel.ATTRIBUTE_KEY);
        url.addParams(this.url.params());
        virtue.eventDispatcher().dispatch(new SendMessageEvent(() -> sendError(invocation, channel, cause)));
    }

    protected abstract void sendSuccess(Invocation invocation, Channel channel, Object result) throws RpcException;

    protected abstract void sendError(Invocation invocation, Channel channel, Throwable cause) throws RpcException;

}
