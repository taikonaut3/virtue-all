package io.virtue.common.module;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/26 16:23
 */
public interface ModuleExceptionFactory<E extends Throwable> {

    E create(String msg);

    E create(Throwable cause);

    E create(String msg, Throwable cause);
}
