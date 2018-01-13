package uk.co.gencoreoperative.btw.utils;

import java.util.Map;
import java.util.function.Supplier;

/**
 * A {@link Supplier} like interface that describes the ability to supply an instance
 * of type {@code O}, or if there was an error in this process to throw an {@link Exception}.
 *
 * The decision here is to keep exception handling in functions that use this interface
 * simple. There is a lot of complexity around handling exceptions or describing them
 * with generics which are avoided by not opening the box in the first place.
 *
 * @param <O> The type of output yielded by this supplier.
 */
@FunctionalInterface
public interface ThrowingSupplier<O> {
    O getOrThrow(Map<Class, Object> inputs) throws Exception;
}
