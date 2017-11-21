package uk.co.gencoreoperative.btw.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

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
public abstract class AbstractCommand<T> extends Observable {
    private final String description;
    private final Class output;
    private final Set<Class> inputs = new HashSet<>();

    private final AtomicBoolean success = new AtomicBoolean();
    private final AtomicBoolean processed  = new AtomicBoolean(false);
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicReference<T> result = new AtomicReference<>();
    private String error;

    public AbstractCommand(String description, Class output, Class... inputs) {
        this.description = description;
        this.output = output;
        this.inputs.addAll(Arrays.asList(inputs));
    }

    /**
     * Each command needs to define the input objects it requires for processing.
     *
     * @see CommandManager for details about the wiring of commands together.
     *
     * @return Non null, possibly empty set of classes.
     */
    public Set<Class> input() {
        return inputs;
    }

    /**
     * Each command needs to define the output value it produces.
     *
     * @see CommandManager for details about the wiring of commands together.
     *
     * @return Possibly null response which indicates no response.
     */
    public Class output() {
        return output;
    }

    /**
     * Process the action.
     *
     * If the action fails due to {@link Exception} being thrown, then extract the message from the error and
     * indicate unsuccessful.
     *
     * If the action was cancelled, then we will indicate cancelled and unsuccessful.
     *
     * Otherwise the action was successful.
     */
    // TODO: JavaDoc
    public Optional<T> process(Map<Class, Object> inputs) throws Exception {
        try {
            result.set(processAction(inputs));
            if (result.get() == null) {
                cancelled.set(true);
            } else {
                success.set(true);
                return Optional.of(result.get());
            }
        } catch (Exception e) {
            // TODO: Throw exception
            success.set(false);
            error = e.getMessage();
        } finally {
            processed.set(true);
        }
        return Optional.empty();
    }

    /**
     * Create a promise that a caller can be issued with. The result of the command will
     * be yielded on request. By invoking the {@link Supplier} of this method, the
     * associated command will be processed.
     *
     * @return A supplier which will yield an {@link Optional} of the processed value only
     * if the processing was successful. If there was any error (exception or cancelled) then
     * the optional will be empty.
     */
    public Supplier<Optional<T>> promise() {
        return () -> {
//            if (!isProcessed()) {
//                process();
//            }
//            if (isSuccess()) {
//                return Optional.of(result.get());
//            }
            return Optional.empty();
        };
    }

    /**
     * Indicates if the action has been processed yet. Attempting to determine the state of the
     * action (success/error) before the command has been processed will result in an error.
     * @return True if the state of processing is ready to collect. False if not.
     */
    public boolean isProcessed() {
        return processed.get();
    }

    /**
     * @return True if the processed command was successful, otherwise false.
     */
    public boolean isSuccess() {
        if (!isProcessed()) throw new IllegalStateException("Command not processed yet");
        return success.get();
    }

    /**
     * @return If the user cancelled the action, this will be true.
     */
    public boolean isCancelled() {
        if (!isProcessed()) throw new IllegalStateException("Command not processed yet");
        return cancelled.get();
    }

    /**
     * @return If {@link #isSuccess()} is false, this will return the reason for the failure.
     */
    public String getFailedReason() {
        if (!isProcessed()) throw new IllegalStateException("Command not processed yet");
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
    // TODO: JavaDoc
    protected abstract T processAction(Map<Class, Object> inputs) throws Exception;
}
