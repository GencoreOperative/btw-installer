/*
 * Copyright 2017 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package uk.co.gencoreoperative.btw.utils;

import java.util.function.Supplier;

/**
 * A {@link Supplier} like interface that describes the ability to supply an instance
 * of type {@code T}, or if there was an error in this process to throw an {@link Exception}.
 *
 * The decision here is to keep exception handling in functions that use this interface
 * simple. There is a lot of complexity around handling exceptions or describing them
 * with generics which are avoided by not opening the box in the first place.
 *
 * @param <T> The type yielded by this supplier.
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {
    T getOrThrow() throws Exception;
}
