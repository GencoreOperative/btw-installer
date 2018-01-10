package uk.co.gencoreoperative.btw.command;

import java.util.Map;

import uk.co.gencoreoperative.btw.utils.ThrowingSupplier;

/**
 * Describes a command which is performed by the system, for example a
 * file system operation.
 *
 * This command can fail for a known reason and if this is the case
 * an exception will capture the detail.
 */
public class SystemCommand<T> extends AbstractCommand<T> {
    private ThrowingSupplier<T> action;

    public <E extends Exception> SystemCommand(ThrowingSupplier<T> action, String description, Class output, Class... inputs) {
        super(description, output, inputs);
        this.action = action;
    }

    /**
     * Process the system action by getting the result of the provided supplier.
     *
     * @return The result T of the supplier if it was successful.
     * @throws Exception If the operation failed.
     */
    @Override
    protected T processAction(Map<Class, Object> inputs) throws Exception {
        return action.getOrThrow(inputs);
    }

    /**
     * @return false as {@link SystemCommand} do not involve user interaction.
     */
    @Override
    protected boolean canCancel() {
        return false;
    }
}
