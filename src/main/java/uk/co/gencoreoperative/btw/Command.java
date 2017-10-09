/*
 * Copyright 2017 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package uk.co.gencoreoperative.btw;


import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

import uk.co.gencoreoperative.btw.utils.ThrowingSupplier;

/**
 * Models a command which can be executed by the utility. A {@link Command} is a
 * value object in that it contains the state of the processing.
 *
 * The command consists of three parts:
 * <ol>
 *     <li>An action is performed which generates an output of type T</li>
 *     <li>A validator which validates the output is correct</li>
 *     <li>A description for display purposes</li>
 * </ol>
 *
 * The command has a number of statesL
 * <ul>
 *     <li>Not Processed - Initial state</li>
 *     <li>Successful - Processed and generated result</li>
 *     <li>Failed - Error reason captured</li>
 *     <li>Cancelled - User cancelled the command</li>
 * </ul>
 *
 * @param <T> The type of output the Command action generates.
 */
public class Command<T, E extends Throwable> extends Observable {
    private final ThrowingSupplier<T, E> action;
    private final Predicate<T> validator;
    private final String description;

    private final AtomicBoolean success = new AtomicBoolean();
    private final AtomicBoolean processed  = new AtomicBoolean(false);
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicReference<T> result = new AtomicReference<>();

    public Command(ThrowingSupplier<T, E> action, Predicate<T> validator, String description) {
        this.action = action;
        this.validator = validator;
        this.description = description;
    }

    /**
     * Executes the command by attempting to get the result of the {@link #action}.
     *
     * If this result is validated the command will move to the successful state.
     * If the
     *
     * This might result in failure, in which case this is thrown as a
     * {@link ProcessingFailedException}.
     *
     * If the result did not fail, then the user
     * might cancel the command (for those that require user interaction).
     *
     * and sets status as a result of execution.
     *
     * TODO: Add a way of a command signalling it has been cancelled rather than error
     * TODO: Add an error reason to failure state
     */
    public void process() throws E {
        result.set(action.getOrThrow());
        processed.set(true);
        if (result.get() == null) return; // Null after action signals cancelled or failed.
        success.set(validator.test(result.get()));
        setChanged();
        notifyObservers(this);
    }

    /**
     * Create a promise that a caller can be issued with. The result of the command will
     * be yielded on request. This assumes that the command has been processed by calling
     * the {@link #process()} method.
     *
     * @return A supplier which will yield the processed value only if this Command
     * has been processed. That is this will only return a value when
     * {@link Command#isProcessed() is true.
     */
    Supplier<T> promise() {
        return () -> {
            if (!isProcessed()) throw new IllegalStateException();
            return result.get();
        };
    }

    /**
     * @return True if the Command has been processed.
     */
    public boolean isProcessed() {
        return processed.get();
    }

    /**
     * @return True if the result of processing was successful.
     */
    public boolean isSuccessful() {
        if (!isProcessed()) throw new IllegalStateException("Not processed yet");
        return success.get();
    }

    /**
     * @return Human readable description of the Command.
     */
    public String getDescription() {
        return description;
    }
}
