package io.virtue.rpc.support;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.util.AssertUtil;
import io.virtue.common.util.ReflectUtil;
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

    private MethodAccess methodAccess;

    private Map<Method, Integer> methodIndex;

    private String remoteServiceName;

    @SuppressWarnings("unchecked")
    public ComplexRemoteService(Virtue virtue, T target) {
        super(virtue);
        AssertUtil.notNull(target);
        AssertUtil.condition(checkRemoteService(target.getClass()), "remoteService this Method Only support @RemoteService modifier's Object");
        this.target = target;
        this.remoteServiceClass = (Class<T>) target.getClass();
        init();

    }

    public static boolean checkRemoteService(Class<?> remoteServiceClass) {
        return remoteServiceClass.isAnnotationPresent(REMOTE_SERVICE_CLASS);
    }

    @Override
    public void init() {
        // parse @RemoteService
        parseRemoteService();
        methodIndex = new HashMap<>();
        methodAccess = MethodAccess.get(target.getClass());
        // parse ServerCaller
        parseServerCaller();
    }

    private void parseRemoteService() {
        io.virtue.core.annotation.RemoteService service = remoteServiceClass.getAnnotation(REMOTE_SERVICE_CLASS);
        remoteApplication = virtue.applicationName();
        remoteServiceName = service.value();
        proxy = service.proxy();

    }

    private void parseServerCaller() {
        for (Method method : remoteServiceClass.getDeclaredMethods()) {
            InvokerFactory factoryProvider = ReflectUtil.findAnnotation(method, InvokerFactory.class);
            if (factoryProvider != null) {
                io.virtue.core.InvokerFactory invokerFactory = ExtensionLoader.loadService(io.virtue.core.InvokerFactory.class, factoryProvider.value());
                Callee<?> caller = invokerFactory.createCallee(method, this);
                if (caller != null) {
                    invokers.put(method, caller);
                    addInvokerMapping(caller.protocol(), caller.path(), caller);
                    methodIndex.put(method, methodAccess.getIndex(method.getName(), method.getParameterTypes()));
                    virtue.configManager().serverConfigManager().neededOpenProtocol(caller.protocol());
                }
            }
        }
    }

    @Override
    public Object invokeMethod(Method method, Object[] args) {
        Integer methodIndex = this.methodIndex.get(method);
        return methodAccess.invoke(target,methodIndex,args);
    }

    @Override
    public T target() {
        return target;
    }

    @Override
    public String name() {
        return remoteServiceName;
    }

}
