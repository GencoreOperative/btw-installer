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
 * A {@link Supplier} like interface that describes the ability to supply an
 * instance of type T, or if there was an error in this process to throw an
 * exception to type E.
 *
 * @param <T> The type yielded by this supplier.
 * @param <E> The type thrown in the event of a failure.
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
    T getOrThrow() throws E;
}
