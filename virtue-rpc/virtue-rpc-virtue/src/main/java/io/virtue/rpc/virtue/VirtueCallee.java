package io.virtue.rpc.virtue;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.url.URL;
import io.virtue.common.util.GenerateUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteService;
import io.virtue.rpc.support.AbstractCallee;
import io.virtue.rpc.virtue.config.VirtueCallable;
import io.virtue.rpc.virtue.envelope.VirtueResponse;
import io.virtue.transport.Response;
import io.virtue.transport.channel.Channel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * Virtue protocol Callee.
 */
@Getter
@Accessors(fluent = true)
public class VirtueCallee extends AbstractCallee<VirtueCallable, VirtueProtocol> {

    private static final Logger logger = LoggerFactory.getLogger(VirtueCallee.class);

    private String remoteServiceName;

    private String callMethod;

    public VirtueCallee(Method method, RemoteService<?> remoteService) {
        super(method, remoteService, VIRTUE, VirtueCallable.class);
    }

    @Override
    public void doInit() {
        this.remoteServiceName = remoteService().name();
        this.callMethod = StringUtil.isBlankOrDefault(parsedAnnotation.name(), GenerateUtil.generateKey(method()));
    }

    @Override
    public List<String> pathList() {
        return List.of(remoteServiceName, callMethod);
    }

    @Override
    protected void sendSuccess(Invocation invocation, Channel channel, Object result) throws RpcException {
        URL url = invocation.url();
        Object message = url.get(Key.SERVICE_RESPONSE);
        Response response = Response.success(url, message);
        sendResponse(channel, response);
    }

    @Override
    protected void sendError(Invocation invocation, Channel channel, Throwable cause) throws RpcException {
        URL url = invocation.url();
        VirtueResponse message = (VirtueResponse) url.get(Key.SERVICE_RESPONSE);
        message.body("Server Exception message: " + cause.getMessage());
        url.addParam(Key.BODY_TYPE, String.class.getName());
        Response response = Response.error(url, message);
        sendResponse(channel, response);
    }

    private void sendResponse(Channel channel, Response response) {
        String responseContextStr = RpcContext.responseContext().toString();
        response.url().addParam(Key.RESPONSE_CONTEXT, responseContextStr);
        channel.send(response);
    }
}
