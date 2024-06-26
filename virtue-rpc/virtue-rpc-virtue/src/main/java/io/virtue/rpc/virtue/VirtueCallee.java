package io.virtue.rpc.virtue;

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

import static io.virtue.common.constant.Components.Protocol.VIRTUE;

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

}
