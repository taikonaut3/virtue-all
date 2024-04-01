package io.virtue.rpc.support;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.util.AssertUtil;
import io.virtue.common.util.GenerateUtil;
import io.virtue.common.util.ReflectionUtil;
import io.virtue.core.Callee;
import io.virtue.core.RemoteService;
import io.virtue.core.Virtue;
import io.virtue.core.annotation.InvokerFactory;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
        this.remoteServiceClass = (Class<T>) target.getClass();
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

    private void parseRemoteService() {
        var service = remoteServiceClass.getAnnotation(REMOTE_SERVICE_CLASS);
        remoteApplication = virtue.applicationName();
        remoteServiceName = service.value();
        proxy = service.proxy();

    }

    private void parseServerCaller() {
        for (Method method : remoteServiceClass.getDeclaredMethods()) {
            InvokerFactory factoryProvider = ReflectionUtil.findAnnotation(method, InvokerFactory.class);
            if (factoryProvider != null) {
                var invokerFactory = ExtensionLoader.loadService(io.virtue.core.InvokerFactory.class, factoryProvider.value());
                Callee<?> callee = invokerFactory.createCallee(method, this);
                if (callee != null) {
                    invokers.put(method, callee);
                    addInvokerMapping(callee.protocol(), callee.path(), callee);
                    methodIndex.put(method, methodAccess.getIndex(method.getName(), method.getParameterTypes()));
                    virtue.configManager().serverConfigManager().neededOpenProtocol(callee.protocol());
                }
            }
        }
    }

    @Override
    public Object invokeMethod(Method method, Object[] args) {
        Integer methodIndex = this.methodIndex.get(method);
        return methodAccess.invoke(target, methodIndex, args);
    }

    @Override
    public Callee<?> getCallee(String protocol, String path) {
        String mapping = GenerateUtil.generateCalleeMapping(protocol, path);
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

    private void addInvokerMapping(String protocol, String path, Callee<?> callee) {
        String mapping = GenerateUtil.generateCalleeMapping(protocol, path);
        mappingCallee.put(mapping, callee);
    }

}
