package io.virtue.rpc.virtue;

import io.virtue.common.util.GenerateUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.config.RemoteCaller;
import io.virtue.config.annotation.Config;
import io.virtue.config.annotation.Options;
import io.virtue.rpc.config.AbstractClientCaller;
import io.virtue.rpc.virtue.config.VirtueCall;
import io.virtue.common.constant.Components;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

@Getter
public class VirtueClientCaller extends AbstractClientCaller<VirtueCall> {

    private static final Logger logger = LoggerFactory.getLogger(VirtueClientCaller.class);

    private String remoteService;

    private String callMethod;

    public VirtueClientCaller(Method method, RemoteCaller<?> remoteCaller) {
        super(method, remoteCaller, Components.Protocol.VIRTUE, VirtueCall.class);
    }

    @Override
    public void doInit() {
        remoteService = parsedAnnotation.service();
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
        return List.of(remoteService, callMethod);
    }
}
