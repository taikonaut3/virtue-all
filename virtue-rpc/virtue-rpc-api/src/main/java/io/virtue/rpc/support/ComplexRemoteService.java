package io.virtue.rpc.support;

import io.virtue.rpc.support.reflect.MethodAccess;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.util.AssertUtil;
import io.virtue.common.util.GenerateUtil;
import io.virtue.common.util.ReflectionUtil;
import io.virtue.core.Callee;
import io.virtue.core.RemoteService;
import io.virtue.core.Virtue;
import io.virtue.core.annotation.Protocol;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default RemoteService Impl.
 *
 * @param <T> target type
 */
@ToString
public class ComplexRemoteService<T> extends AbstractInvokerContainer implements RemoteService<T> {

    private static final Class<io.virtue.core.annotation.RemoteService> REMOTE_SERVICE_CLASS =
            io.virtue.core.annotation.RemoteService.class;

    private final Class<T> remoteServiceClass;

    private final T target;

    private final MethodAccess methodAccess;

    private final Map<Method, Integer> methodIndex;

    private final Map<String, Callee<?>> mappingCallee = new HashMap<>();

    private String remoteServiceName;

    @SuppressWarnings("unchecked")
    public ComplexRemoteService(Virtue virtue, T target) {
        super(virtue);
        AssertUtil.notNull(target);
        AssertUtil.condition(checkRemoteService(target.getClass()),
                "remoteService this Method Only support @RemoteService modifier's Object");
        this.target = target;
        this.remoteServiceClass = (Class<T>) ReflectionUtil.getTargetClass(target.getClass());
        this.methodIndex = new HashMap<>();
        this.methodAccess = MethodAccess.get(target.getClass());
        init();

    }

    private static boolean checkRemoteService(Class<?> remoteServiceClass) {
        return remoteServiceClass.isAnnotationPresent(REMOTE_SERVICE_CLASS);
    }

    @Override
    public void init() {
        // parse @RemoteService
        parseRemoteService();
        // parse ServerCaller
        parseServerCaller();
    }

    @Override
    public void stop() {
        super.stop();
        mappingCallee.clear();
    }

    @Override
    public Object invokeMethod(Method method, Object[] args) {
        Integer methodIndex = this.methodIndex.get(method);
        return methodAccess.invoke(target, methodIndex, args);
    }

    @Override
    public Callee<?> getCallee(String protocol, String path) {
        String mapping = GenerateUtil.generateCalleeMapping(protocol, path);
        for (String key : mappingCallee.keySet()) {
            if (match(mapping, key)) {
                return mappingCallee.get(key);
            }
        }
        return mappingCallee.get(mapping);
    }

    @Override
    public T target() {
        return target;
    }

    @Override
    public String name() {
        return remoteServiceName;
    }

    private boolean match(String str, String pattern) {
        // 将模式中的 {} 替换为正则表达式的匹配规则，其中 \\{([^{}]*)\\} 表示匹配 { 和 } 之间的任意内容
        String regex = pattern.replaceAll("\\{([^{}]*)\\}", "([^/]+)")
                .replaceAll("\\{([^{}]*)$", "\\\\$0")
                .replaceAll("(?<!\\})\\}$", "\\\\$0");
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    private void parseRemoteService() {
        var service = remoteServiceClass.getAnnotation(REMOTE_SERVICE_CLASS);
        remoteApplication = virtue.applicationName();
        remoteServiceName = service.value();
        proxy = service.proxy();

    }

    private void parseServerCaller() {
        for (Method method : remoteServiceClass.getDeclaredMethods()) {
            Protocol protocol = ReflectionUtil.findAnnotation(method, Protocol.class);
            if (protocol != null) {
                var protocolInstance = ExtensionLoader.loadExtension(io.virtue.rpc.protocol.Protocol.class, protocol.value());
                Callee<?> callee = protocolInstance.createCallee(method, this);
                if (callee != null) {
                    invokers.put(method, callee);
                    addInvokerMapping(callee.protocol(), callee.path(), callee);
                    methodIndex.put(method, methodAccess.getIndex(method.getName(), method.getParameterTypes()));
                    virtue.configManager().serverConfigManager().neededOpenProtocol(callee.protocol());
                }
            }
        }
    }

    private void addInvokerMapping(String protocol, String path, Callee<?> callee) {
        String mapping = GenerateUtil.generateCalleeMapping(protocol, path);
        mappingCallee.put(mapping, callee);
    }

}
