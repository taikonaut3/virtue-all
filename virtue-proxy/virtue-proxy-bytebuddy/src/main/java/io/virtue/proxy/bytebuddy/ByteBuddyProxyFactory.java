package io.virtue.proxy.bytebuddy;

import io.virtue.common.exception.RpcException;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.proxy.AbstractProxyFactory;
import io.virtue.proxy.InvocationHandler;
import io.virtue.common.constant.Components;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Create Proxy  performance is not good
 */
@ServiceProvider(Components.ProxyFactory.BYTEBUDDY)
public class ByteBuddyProxyFactory extends AbstractProxyFactory {

    @Override
    protected <T> T doCreateProxy(Class<T> interfaceClass, InvocationHandler handler) {
        try (DynamicType.Unloaded<T> dynamicType = new ByteBuddy()
                .subclass(interfaceClass)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(new MethodInterceptor(handler).new InterfaceInterceptor()))
                .make()) {
            return dynamicType.load(interfaceClass.getClassLoader())
                    .getLoaded().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RpcException("Create Proxy fail for interface: " + interfaceClass.getName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(T target, InvocationHandler handler) {
        try (DynamicType.Unloaded<T> dynamicType = (DynamicType.Unloaded<T>) new ByteBuddy()
                .subclass(target.getClass())
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(new MethodInterceptor(handler).new InstanceInterceptor()))
                .make()) {
            return dynamicType.load(target.getClass().getClassLoader())
                    .getLoaded().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RpcException("Create Proxy fail for target: " + target.getClass().getName(), e);
        }
    }

}