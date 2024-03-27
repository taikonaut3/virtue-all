package io.virtue.common.util;

import io.virtue.common.exception.CommonException;
import io.virtue.common.module.Module;
import io.virtue.common.module.ModuleExceptionFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/26 16:04
 */
public class ExceptionUtil {

    private static final Map<Module, ModuleExceptionFactory<?>> MODULE_EXCEPTION_FACTORY_MAP = new HashMap<>();

    public static <E extends Throwable> void register(Module module, ModuleExceptionFactory<E> factory) {
        MODULE_EXCEPTION_FACTORY_MAP.put(module, factory);
    }

    public static Throwable unwrap(Module module, Throwable wrapped) {
        return unwrap(module, null, wrapped);
    }

    public static Throwable unwrap(Module module, String msg) {
        return unwrap(module, msg, null);
    }

    public static Throwable unwrap(Module module, String msg, Throwable wrapped) {
        ModuleExceptionFactory<?> factory = MODULE_EXCEPTION_FACTORY_MAP.get(module);
        Throwable unwrapped = wrapped;
        while (true) {
            switch (unwrapped) {
                case InvocationTargetException e -> unwrapped = e.getTargetException();
                case UndeclaredThrowableException e -> unwrapped = e.getUndeclaredThrowable();
                case ExecutionException e -> unwrapped = e.getCause();
                case CommonException e -> {
                    return e;
                }
                default -> {
                    return factory == null
                            ? new CommonException(unwrapped)
                            : (StringUtil.isBlank(msg) ? factory.create(unwrapped) : factory.create(msg, unwrapped));
                }
            }
        }
    }

}
