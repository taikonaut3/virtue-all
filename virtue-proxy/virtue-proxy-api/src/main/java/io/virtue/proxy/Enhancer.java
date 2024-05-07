package io.virtue.proxy;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;

/**
 * @Author WenBo Zhou
 * @Date 2024/5/7 11:04
 */
@Getter
@Setter
@Accessors(fluent = true)
public abstract class Enhancer<T> {

    private String[] methodNames;
    private Class<?>[][] parameterTypes;
    private Class<?>[] returnTypes;

    /**
     * Returns the index of the first method with the specified name and param types.
     *
     * @param methodName
     * @param paramTypes
     * @return
     */
    public int getIndex(String methodName, Class<?>... paramTypes) {
        for (int i = 0, n = methodNames.length; i < n; i++)
            if (methodNames[i].equals(methodName) && Arrays.equals(paramTypes, parameterTypes[i])) return i;
        throw new IllegalArgumentException("Unable to find non-private method: " + methodName + " " + Arrays.toString(paramTypes));
    }

    /**
     * Invokes the method with the specified name and the specified param types.
     *
     * @param instance
     * @param methodName
     * @param parameterTypes
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invokeMethod(T instance, String methodName, Class<?>[] parameterTypes, Object[] args){
        return invokeMethod(instance, getIndex(methodName, parameterTypes), args);
    }

    public abstract Object invokeMethod(T instance, int index, Object[] args);

}
