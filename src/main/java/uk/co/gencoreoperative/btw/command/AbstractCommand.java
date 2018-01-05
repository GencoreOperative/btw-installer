package uk.co.gencoreoperative.btw.command;

import static java.text.MessageFormat.format;

import java.text.MessageFormat;
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
 *     <li><b>Success</b>: The {@link #process(Map)} method has been called and processing was successful</li>
 *     <li><b>Failed</b>: When {@link #process(Map)} was called but something went wrong. The error is captured.</li>
 *     <li><b>Cancelled</b>: The user cancelled the action in some way.</li>
 * </ul>
 *
 * All commands are able to describe their state to an {@link java.util.Observer} which
 * will be updated after any state change in the command.
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

    /**
     * Create a command which describes what it does, what inputs it requires and
     * what it will output when processing completes.
     *
     * @param description A non null description suitable for display.
     * @param output A possibly null output class.
     * @param inputs A possibly null collection of input classes.
     */
    public AbstractCommand(String description, Class<T> output, Class... inputs) {
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
     * A call to this method will trigger the {@link #processAction(Map)} abstract method
     * which will do the work of the action.
     *
     * After this method completes, the action will be in a new state which will either be
     * successful, or unsuccessful and a reason why.
     *
     * @param inputs A non null. but possibly empty map of inputs which will be
     *               passed to {@link #processAction(Map)}.
     * @return Optional of type T
     */
    public Optional<T> process(Map<Class, Object> inputs) {
        try {
            result.set(processAction(inputs));
            if (result.get() == null) {
                cancelled.set(true);
            } else {
                success.set(true);
                return Optional.of(result.get());
            }
        } catch (Exception e) {
            success.set(false);
            error = e.getMessage();
        } finally {
            processed.set(true);
            notifyObservers();
        }
        return Optional.empty();
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
     * <ul>
     *  <li>If the action was <b>successful</b>, return the result of Type T.</li>
     *  <li>If the action could not be started because of <b>invalid state</b>, throw an exception.</li>
     *  <li>If the action was processed by <b>validation</b> indicated it was not successful, throw an exception.</li>
     *  <li>If the user <b>cancels</b> the action, return null.</li>
     * </ul>
     *
     * @return The result according to the processing of the action implementation and the
     * states abovee.
     *
     * @throws Exception If there was any error in processing which prevents subsequent processing, throw an
     * exception. <i>Note:</i> We are choosing {@link Exception} here for simplicity sake. Promoting to a generic
     * type comes with its own problems which we are choosing to avoid by keeping this simple.
     */
    protected abstract T processAction(Map<Class, Object> inputs) throws Exception;

    @Override
    public String toString() {
        return format("{0} [{1}]", description, isProcessed());
    }
}
