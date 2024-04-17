package io.virtue.core.support;

import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.Invocation;
import io.virtue.core.Invoker;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * The default Invocation implementation class.
 */
@Getter
@Accessors(fluent = true, chain = true)
public class TransferableInvocation implements Invocation {

    @Setter
    private transient Invoker<?> invoker;

    @Setter
    private Object[] args;

    private transient Type returnType;

    private transient Type[] parameterTypes;

    private transient URL url;

    private transient Supplier<Object> invoke;

    public TransferableInvocation() {

    }

    public TransferableInvocation(Caller<?> caller, Object[] args) {
        basic(caller, args);
        this.url = caller.url().replicate();
    }

    public TransferableInvocation(URL url, Callee<?> callee, Object[] args) {
        basic(callee, args);
        this.url = url;
    }

    @Override
    public Object invoke() {
        if (invoke != null) {
            return invoke.get();
        }
        return null;
    }

    @Override
    public Invocation revise(Supplier<Object> invoke) {
        this.invoke = invoke;
        return this;
    }

    /**
     * Adaptation serialization.
     *
     * @return
     */
    public Object[] getArgs() {
        return args();
    }

    /**
     * Adaptation serialization.
     *
     * @param args
     */
    public void setArgs(Object[] args) {
        args(args);
    }

    protected void basic(Invoker<?> invoker, Object[] args) {
        this.invoker = invoker;
        this.args = args;
        this.returnType = invoker.returnType();
        this.parameterTypes = invoker.method().getGenericParameterTypes();
    }

    protected void url(URL url) {
        this.url = url;
    }

}
