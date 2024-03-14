package io.virtue.rpc.virtue;

import io.virtue.common.util.GenerateUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.config.RemoteService;
import io.virtue.config.annotation.Config;
import io.virtue.rpc.config.AbstractServerCaller;
import io.virtue.rpc.virtue.config.VirtueCallable;
import io.virtue.common.constant.Components;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

public class VirtueServerCaller extends AbstractServerCaller<VirtueCallable> {

    private static final Logger logger = LoggerFactory.getLogger(VirtueServerCaller.class);

    private String remoteService;

    private String callMethod;

    public VirtueServerCaller(Method method, RemoteService<?> remoteService) {
        super(method, remoteService, Components.Protocol.VIRTUE, VirtueCallable.class);
    }

    @Override
    public void doInit() {
        this.remoteService = remoteService().name();
        this.callMethod = StringUtil.isBlank(parsedAnnotation.name()) ?
                GenerateUtil.generateKey(method()) : parsedAnnotation.name();
    }

    @Override
    protected Config config() {
        return parsedAnnotation.config();
    }

    @Override
    public List<String> pathList() {
        return List.of(remoteService, callMethod);
    }
}
