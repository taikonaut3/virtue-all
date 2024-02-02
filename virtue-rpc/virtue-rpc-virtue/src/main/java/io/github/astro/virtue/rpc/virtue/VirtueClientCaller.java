package io.github.astro.virtue.rpc.virtue;

import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.RemoteCaller;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.Options;
import io.github.astro.virtue.config.util.GenerateUtil;
import io.github.astro.virtue.rpc.config.AbstractClientCaller;
import io.github.astro.virtue.rpc.virtue.config.VirtueCall;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

@Getter
@ToString
public class VirtueClientCaller extends AbstractClientCaller<VirtueCall> {

    private static final Logger logger = LoggerFactory.getLogger(VirtueClientCaller.class);

    private String remoteService;

    private String callMethod;

    public VirtueClientCaller(Method method, RemoteCaller<?> remoteCaller) {
        super(method, remoteCaller, VIRTUE, VirtueCall.class);
    }

    @Override
    public void doInit() {
        remoteService = parsedAnnotation.service();
        callMethod = StringUtil.isBlank(parsedAnnotation.callMethod()) ? GenerateUtil.generateKey(method()) : parsedAnnotation.callMethod();
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
