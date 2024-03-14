package io.virtue.common.extension;

public class RpcContext extends AbstractAccessor {

    private static final ThreadLocal<RpcContext> THREAD_LOCAL = ThreadLocal.withInitial(RpcContext::new);

    public static RpcContext getContext() {
        return THREAD_LOCAL.get();
    }

    public RpcContext remove(String key) {
        super.remove(key);
        return this;
    }

    public void clear() {
        accessor.clear();
        THREAD_LOCAL.remove();
    }

}
