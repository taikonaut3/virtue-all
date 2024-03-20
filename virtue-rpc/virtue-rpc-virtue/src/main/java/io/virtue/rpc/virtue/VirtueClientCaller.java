package io.virtue.rpc.virtue;

import io.virtue.common.constant.Components;
import io.virtue.common.util.GenerateUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.core.RemoteCaller;
import io.virtue.core.annotation.Config;
import io.virtue.core.annotation.Options;
import io.virtue.rpc.support.AbstractClientCaller;
import io.virtue.rpc.virtue.config.VirtueCall;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class VirtueClientCaller extends AbstractClientCaller<VirtueCall> {

    private static final Logger logger = LoggerFactory.getLogger(VirtueClientCaller.class);

    private String remoteServiceName;

    private String callMethod;

    public VirtueClientCaller(Method method, RemoteCaller<?> remoteCaller) {
        super(method, remoteCaller, Components.Protocol.VIRTUE, VirtueCall.class);
    }

    @Override
    public void doInit() {
        remoteServiceName = parsedAnnotation.service();
        callMethod = StringUtil.isBlank(parsedAnnotation.callMethod()) ?
                GenerateUtil.generateKey(method()) : parsedAnnotation.callMethod();
    }

    @Override
    protected Config config() {
        return parsedAnnotation.config();
    }


    @Override
    protected Options options() {
        return parsedAnnotation.options();
    }

    @Override
    public List<String> pathList() {
        return List.of(remoteServiceName, callMethod);
    }
}
