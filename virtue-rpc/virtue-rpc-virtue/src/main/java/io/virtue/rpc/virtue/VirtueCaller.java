package io.virtue.rpc.virtue;

import io.virtue.common.util.GenerateUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteCaller;
import io.virtue.rpc.support.AbstractCaller;
import io.virtue.rpc.virtue.config.VirtueCall;
import io.virtue.transport.Request;
import io.virtue.transport.RpcFuture;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * Virtue protocol caller.
 */
@Getter
@Accessors(fluent = true)
public class VirtueCaller extends AbstractCaller<VirtueCall, VirtueProtocol> {

    private static final Logger logger = LoggerFactory.getLogger(VirtueCaller.class);

    private String remoteServiceName;

    private String callMethod;

    public VirtueCaller(Method method, RemoteCaller<?> remoteCaller) {
        super(method, remoteCaller, VIRTUE, VirtueCall.class);
    }

    @Override
    public void doInit() {
        remoteServiceName = parsedAnnotation.service();
        callMethod = StringUtil.isBlankOrDefault(parsedAnnotation.callMethod(), GenerateUtil.generateKey(method()));
    }

    @Override
    public List<String> pathList() {
        return List.of(remoteServiceName, callMethod);
    }

    @Override
    protected void send(RpcFuture future) {
        Invocation invocation = future.invocation();
        Object message = protocolInstance.createRequest(invocation);
        Request request = new Request(invocation.url(), message);
        future.client().send(request);
    }
}
