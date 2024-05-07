package io.virtue.proxy.bytebuddy;

import io.virtue.common.extension.spi.Extension;
import io.virtue.common.util.ReflectionUtil;
import io.virtue.proxy.AbstractProxyFactory;
import io.virtue.proxy.Enhancer;
import io.virtue.proxy.EnhancerClassLoader;
import io.virtue.proxy.InvocationHandler;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import static io.virtue.common.constant.Components.ProxyFactory.BYTEBUDDY;
import static net.bytebuddy.jar.asm.Opcodes.*;

/**
 * Create Proxy performance is not good.
 */
@Extension(BYTEBUDDY)
public class ByteBuddyProxyFactory extends AbstractProxyFactory {

    @Override
    protected <T> T doCreateProxy(Class<T> interfaceClass, InvocationHandler handler) throws Exception {
        try (DynamicType.Unloaded<T> dynamicType = new ByteBuddy()
                .subclass(interfaceClass)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(new MethodInterceptor(handler).new InterfaceInterceptor()))
                .make()) {
            return dynamicType.load(interfaceClass.getClassLoader())
                    .getLoaded().getDeclaredConstructor().newInstance();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(T target, InvocationHandler handler) throws Exception {
        try (DynamicType.Unloaded<T> dynamicType = (DynamicType.Unloaded<T>) new ByteBuddy()
                .subclass(target.getClass())
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(new MethodInterceptor(handler).new InstanceInterceptor()))
                .make()) {
            return dynamicType.load(target.getClass().getClassLoader())
                    .getLoaded().getConstructor().newInstance();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> Enhancer<T> createEnhancer(Class<T> type) {
        boolean isInterface = type.isInterface();
        if (!isInterface && type.getSuperclass() == null && type != Object.class)
            throw new IllegalArgumentException("The type must not be an interface, a primitive type, or void.");

        ArrayList<Method> methods = new ArrayList<>();
        if (!isInterface) {
            Class<?> nextClass = type;
            while (nextClass != Object.class) {
                addDeclaredMethodsToList(nextClass, methods);
                nextClass = nextClass.getSuperclass();
            }
        } else
            recursiveAddInterfaceMethodsToList(type, methods);

        int n = methods.size();
        String[] methodNames = new String[n];
        Class<?>[][] parameterTypes = new Class<?>[n][];
        Class<?>[] returnTypes = new Class<?>[n];
        for (int i = 0; i < n; i++) {
            Method method = methods.get(i);
            methodNames[i] = method.getName();
            parameterTypes[i] = method.getParameterTypes();
            returnTypes[i] = method.getReturnType();
        }

        String className = type.getName();
        String accessClassName = className + "Enhancer";
        if (accessClassName.startsWith("java.")) accessClassName = "reflectasm." + accessClassName;

        Class<?> accessClass;
        EnhancerClassLoader loader = EnhancerClassLoader.get(type);
        synchronized (loader) {
            accessClass = loader.loadAccessClass(accessClassName);
            if (accessClass == null) {
                String accessClassNameInternal = accessClassName.replace('.', '/');
                String classNameInternal = className.replace('.', '/');

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                MethodVisitor mv;
                cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null, "io/virtue/proxy/Enhancer",
                        null);
                {
                    mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                    mv.visitCode();
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKESPECIAL, "io/virtue/proxy/Enhancer", "<init>", "()V", false);
                    mv.visitInsn(RETURN);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
                {
                    mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "invoke",
                            "(Ljava/lang/Object;I[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
                    mv.visitCode();

                    if (!methods.isEmpty()) {
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitTypeInsn(CHECKCAST, classNameInternal);
                        mv.visitVarInsn(ASTORE, 4);

                        mv.visitVarInsn(ILOAD, 2);
                        Label[] labels = new Label[n];
                        for (int i = 0; i < n; i++)
                            labels[i] = new Label();
                        Label defaultLabel = new Label();
                        mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

                        StringBuilder buffer = new StringBuilder(128);
                        for (int i = 0; i < n; i++) {
                            mv.visitLabel(labels[i]);
                            if (i == 0)
                                mv.visitFrame(F_APPEND, 1, new Object[]{classNameInternal}, 0, null);
                            else
                                mv.visitFrame(F_SAME, 0, null, 0, null);
                            mv.visitVarInsn(ALOAD, 4);

                            buffer.setLength(0);
                            buffer.append('(');

                            Class<?>[] paramTypes = parameterTypes[i];
                            Class<?> returnType = returnTypes[i];
                            for (int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++) {
                                mv.visitVarInsn(ALOAD, 3);
                                mv.visitIntInsn(BIPUSH, paramIndex);
                                mv.visitInsn(AALOAD);
                                Type paramType = Type.getType(paramTypes[paramIndex]);
                                switch (paramType.getSort()) {
                                    case Type.BOOLEAN:
                                        mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
                                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", isInterface);
                                        break;
                                    case Type.BYTE:
                                        mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
                                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", isInterface);
                                        break;
                                    case Type.CHAR:
                                        mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
                                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", isInterface);
                                        break;
                                    case Type.SHORT:
                                        mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
                                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", isInterface);
                                        break;
                                    case Type.INT:
                                        mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
                                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", isInterface);
                                        break;
                                    case Type.FLOAT:
                                        mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
                                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", isInterface);
                                        break;
                                    case Type.LONG:
                                        mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
                                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", isInterface);
                                        break;
                                    case Type.DOUBLE:
                                        mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
                                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", isInterface);
                                        break;
                                    case Type.ARRAY:
                                        mv.visitTypeInsn(CHECKCAST, paramType.getDescriptor());
                                        break;
                                    case Type.OBJECT:
                                        mv.visitTypeInsn(CHECKCAST, paramType.getInternalName());
                                        break;
                                }
                                buffer.append(paramType.getDescriptor());
                            }

                            buffer.append(')');
                            buffer.append(Type.getDescriptor(returnType));
                            int invoke;
                            if (isInterface)
                                invoke = INVOKEINTERFACE;
                            else if (Modifier.isStatic(methods.get(i).getModifiers()))
                                invoke = INVOKESTATIC;
                            else
                                invoke = INVOKEVIRTUAL;
                            mv.visitMethodInsn(invoke, classNameInternal, methodNames[i], buffer.toString(), isInterface);

                            switch (Type.getType(returnType).getSort()) {
                                case Type.VOID:
                                    mv.visitInsn(ACONST_NULL);
                                    break;
                                case Type.BOOLEAN:
                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", isInterface);
                                    break;
                                case Type.BYTE:
                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", isInterface);
                                    break;
                                case Type.CHAR:
                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", isInterface);
                                    break;
                                case Type.SHORT:
                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", isInterface);
                                    break;
                                case Type.INT:
                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", isInterface);
                                    break;
                                case Type.FLOAT:
                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", isInterface);
                                    break;
                                case Type.LONG:
                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", isInterface);
                                    break;
                                case Type.DOUBLE:
                                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", isInterface);
                                    break;
                            }

                            mv.visitInsn(ARETURN);
                        }
                        mv.visitLabel(defaultLabel);
                        mv.visitFrame(F_SAME, 0, null, 0, null);
                    }
                    mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
                    mv.visitInsn(DUP);
                    mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                    mv.visitInsn(DUP);
                    mv.visitLdcInsn("Method not found: ");
                    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", isInterface);
                    mv.visitVarInsn(ILOAD, 2);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", isInterface);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", isInterface);
                    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", isInterface);
                    mv.visitInsn(ATHROW);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
                cw.visitEnd();
                byte[] data = cw.toByteArray();
                accessClass = loader.defineAccessClass(accessClassName, data);
            }
        }
        try {
            Enhancer<T> access = (Enhancer<T>) ReflectionUtil.createInstance(accessClass);
            access.methodNames(methodNames);
            access.parameterTypes(parameterTypes);
            access.returnTypes(returnTypes);
            return access;
        } catch (Throwable t) {
            throw new RuntimeException("Error constructing method access Class<?>: " + accessClassName, t);
        }
    }

    private static void addDeclaredMethodsToList(Class<?> type, ArrayList<Method> methods) {
        Method[] declaredMethods = type.getDeclaredMethods();
        for (Method method : declaredMethods) {
            int modifiers = method.getModifiers();
            // if (Modifier.isStatic(modifiers)) continue;
            if (Modifier.isPrivate(modifiers)) continue;
            methods.add(method);
        }
    }

    private static void recursiveAddInterfaceMethodsToList(Class<?> interfaceType, ArrayList<Method> methods) {
        addDeclaredMethodsToList(interfaceType, methods);
        for (Class<?> nextInterface : interfaceType.getInterfaces())
            recursiveAddInterfaceMethodsToList(nextInterface, methods);
    }

}
