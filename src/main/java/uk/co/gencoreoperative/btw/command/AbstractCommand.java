/*
 * Copyright 2017 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package uk.co.gencoreoperative.btw.command;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import uk.co.gencoreoperative.btw.Command;

/**
 * A generic command concept which has the ability to be processed and generate a result
 * of type T. The command has the following states:
 *
 * <ul>
 *     <li><b>Initial</b>: When created the Command will be in this state.</li>
 *     <li><b>Success</b>: The {@link #process()} method has been called and processing was successful</li>
 *     <li><b>Failed</b>: When {@link #process()} was called but something went wrong. The error is captured.</li>
 *     <li><b>Cancelled</b>: The user cancelled the action in some way.</li>
 * </ul>
 */
public abstract class AbstractCommand<T> {
    private final String description;

    private final AtomicBoolean success = new AtomicBoolean();
    private final AtomicBoolean processed  = new AtomicBoolean(false);
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicReference<T> result = new AtomicReference<>();
    private String error;

    public AbstractCommand(String description) {
        this.description = description;
    }

    /**
     * Transition the Command from its initial state to determine if the Command was successful.
     */

    /**
     * Process the action.
     *
     * If the action fails due to {@link Exception} being thrown, then extract the message from the error and
     * indicate unsuccessful.
     *
     * If the action was cancelled, then we will indicate cancelled and unsuccessful.
     *
     * TODO AM HERE
     * This thought was about task linking.
     *
     * We want commands to issue promises so we can link them together. When the promise is
     * called, this invokes the associated command and generates a result. The user of the result
     * of the promise needs to check if this was successful or not before attempting to use the
     * result.
     *
     * So - the question to think about is - is this sort of chaining suitable?
     *
     * - Define tasks - what does the task do? What does it need to do it?
     * - Link tasks to the promised output of a previous task
     * - The last task - invoke its promise to trigger the chain of commands above it.
     */

    private void process() {
        try {
            result.set(processAction());
            processed.set(true);
            if (result.get() == null) {
                cancelled.set(true);
            } else {
                success.set(true);
            }
        } catch (Exception e) {
            success.set(false);
            error = e.getMessage();
        }
    }

    /**
     * Create a promise that a caller can be issued with. The result of the command will
     * be yielded on request. This assumes that the command has been processed by calling
     * the {@link #process()} method.
     *
     * @return A supplier which will yield the processed value only if this Command
     * has been processed. That is this will only return a value when
     * {@link AbstractCommand#process()} has been called.
     */
    public Supplier<T> promise() {
        return () -> {
            process();
            if (isSuccess()) return result.get();
            return null;
        };
    }

    /**
     * @return True if the processed command was successful, otherwise false.
     */
    public boolean isSuccess() {
        if (!processed.get()) throw new IllegalStateException("Command not processed yet");
        return success.get();
    }

    /**
     * @return If the user cancelled the action, this will be true.
     */
    public boolean isCancelled() {
        if (!processed.get()) throw new IllegalStateException("Command not processed yet");
        return cancelled.get();
    }

    /**
     * @return If {@link #isSuccess()} is false, this will return the reason for the failure.
     */
    public String getFailedReason() {
        if (!processed.get()) throw new IllegalStateException("Command not processed yet");
        return error;
    }

    /**
     * @return A textual description of the Command intended for display.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Implementation of the Command action.
     *
     * The implementation needs to manage the specific details of performing the command and associated user
     * interaction that may be required.
     *
     * The convention for an action is as follows:
     *
     * If the action was <b>successful</b>, return the result of Type T.
     * If the action could not be started because of <b>invalid state</b>, throw an exception.
     * If the action was processed by <b>validation</b> indicated it was not successful, throw an exception.
     * If the user <b>cancels</b> the action, return null.
     *
     * @return If the user cancelled the Command, return {@code null} otherwise return the result of type T from
     * processing.
     *
     * @throws Exception If there was any error in processing which prevents subsequent processing, throw an
     * exception. <i>Note:</i> We are choosing {@link Exception} here for simplicity sake. Promoting to a generic
     * type comes with its own problems which we are choosing to avoid by keeping this simple.
     */
    protected abstract T processAction() throws Exception;
}
