package io.github.taikonaut3.virtue.proxy.bytebuddy;

import io.github.taikonaut3.virtue.common.exception.RpcException;
import io.github.taikonaut3.virtue.common.spi.ServiceProvider;
import io.github.taikonaut3.virtue.proxy.AbstractProxyFactory;
import io.github.taikonaut3.virtue.proxy.InvocationHandler;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import static io.github.taikonaut3.virtue.common.constant.Components.ProxyFactory.BYTEBUDDY;

/**
 * Create Proxy  performance is not good
 */
@ServiceProvider(BYTEBUDDY)
public class ByteBuddyProxyFactory extends AbstractProxyFactory {

    @Override
    protected <T> T doCreateProxy(Class<T> interfaceClass, InvocationHandler handler) {
        try (DynamicType.Unloaded<T> dynamicType = new ByteBuddy()
                    .subclass(interfaceClass)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(new MethodInterceptor(interfaceClass, handler).new InterfaceInterceptor()))
                .make()) {
            return dynamicType.load(interfaceClass.getClassLoader())
                    .getLoaded().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RpcException("Create Proxy fail for interface: " + interfaceClass.getName(), e);
        }
    }

    @Override
    protected <T> T doCreateProxy(T target, InvocationHandler handler) {
        try (DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                    .subclass(target.getClass())
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(new MethodInterceptor(target, handler).new InstanceInterceptor()))
                .make()) {
            return (T) dynamicType.load(target.getClass().getClassLoader())
                    .getLoaded().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RpcException("Create Proxy fail for target: " + target.getClass().getName(), e);
        }
    }

}
