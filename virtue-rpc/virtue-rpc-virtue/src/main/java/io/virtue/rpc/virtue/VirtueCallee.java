package io.virtue.rpc.virtue;

import io.virtue.common.constant.Components;
import io.virtue.common.util.GenerateUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.core.RemoteService;
import io.virtue.rpc.support.AbstractCallee;
import io.virtue.rpc.virtue.config.VirtueCallable;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Virtue protocol Callee.
 */
@Getter
@Accessors(fluent = true)
public class VirtueCallee extends AbstractCallee<VirtueCallable> {

    private static final Logger logger = LoggerFactory.getLogger(VirtueCallee.class);

    private String remoteServiceName;

    private String callMethod;

    public VirtueCallee(Method method, RemoteService<?> remoteService) {
        super(method, remoteService, Components.Protocol.VIRTUE, VirtueCallable.class);
    }

    @Override
    public void doInit() {
        this.remoteServiceName = remoteService().name();
        this.callMethod = StringUtil.isBlank(parsedAnnotation.name())
                ? GenerateUtil.generateKey(method()) : parsedAnnotation.name();
    }

    @Override
    public List<String> pathList() {
        return List.of(remoteServiceName, callMethod);
    }
}
