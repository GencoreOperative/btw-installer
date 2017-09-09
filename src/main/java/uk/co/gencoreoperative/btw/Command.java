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

/**
 * Models a command which can be executed by the utility.
 *
 * The command consists of three parts. An action is performed which
 * generates an output. This output is validated to ensure it is correct.
 * Finally a description is used for display purposes.
 *
 * @param <T> The type of output the Command action generates.
 */
public class Command<T> extends Observable {
    private final Supplier<T> action;
    private final Predicate<T> validator;
    private final String description;

    private final AtomicBoolean success = new AtomicBoolean();
    private final AtomicReference<T> result = new AtomicReference<>();

    public Command(Supplier<T> action, Predicate<T> validator, String description) {
        this.action = action;
        this.validator = validator;
        this.description = description;
    }

    /**
     * Executes the command and sets status as a result of execution.
     */
    public void process() {
        result.set(action.get());
        success.set(validator.test(result.get()));
        setChanged();
        notifyObservers(this);
    }

    /**
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
        return result.get() != null;
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
