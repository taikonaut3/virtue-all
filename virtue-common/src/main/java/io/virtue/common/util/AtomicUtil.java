package io.virtue.common.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

/**
 * Utility class for atomic operations.
 */
public class AtomicUtil {

    /**
     * Use the compare And Set method to atomically update the Atomic Long value.
     *
     * @param atomicLong
     * @param updateFunction
     */
    public static long updateAtomicLong(AtomicLong atomicLong, LongUnaryOperator updateFunction) {
        long prevValue, newValue;
        do {
            prevValue = atomicLong.get();
            newValue = updateFunction.applyAsLong(prevValue);
        } while (!atomicLong.compareAndSet(prevValue, newValue));
        return newValue;
    }

    /**
     * Use the compare And Set method to atomically update the Atomic Int value.
     *
     * @param atomicInteger
     * @param updateFunction
     */
    public static int updateAtomicInteger(AtomicInteger atomicInteger, IntUnaryOperator updateFunction) {
        int prevValue, newValue;
        do {
            prevValue = atomicInteger.get();
            newValue = updateFunction.applyAsInt(prevValue);
        } while (!atomicInteger.compareAndSet(prevValue, newValue));
        return newValue;
    }

    /**
     * Use the compare And Set method to atomically update the Atomic Reference value.
     *
     * @param <T>
     * @param atomicReference
     * @param updateFunction
     */
    public static <T> void updateAtomicReference(AtomicReference<T> atomicReference, UnaryOperator<T> updateFunction) {
        T prevValue, newValue;
        do {
            prevValue = atomicReference.get();
            newValue = updateFunction.apply(prevValue);
        } while (!atomicReference.compareAndSet(prevValue, newValue));
    }
}
