package uk.co.gencoreoperative.btw.command;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import uk.co.gencoreoperative.btw.utils.ThrowingSupplier;

/**
 * Describes a command which is completed by user action. This action needs
 * a validation step to verify that the user has performed the action
 * correctly. It also needs to support the concept of being cancelled.
 */
public class UserCommand<T> extends AbstractCommand<T> {
    private final ThrowingSupplier<T> action;
    private final Predicate<T> validator;

    public UserCommand(ThrowingSupplier<T> action, Predicate<T> validator, String description, Class output, Class... inputs) {
        super(description, output, inputs);
        this.action = action;
        this.validator = validator;
    }

    /**
     * Process the user action.
     *
     * @return T if the action was successfully completed, {@code null} if the user cancelled the action.
     * @throws Exception If the validation failed to validate T.
     */
    protected T processAction(Map<Class, Object> inputs) throws Exception {
        T result = action.getOrThrow(inputs);
        if (result == null) {
            // Cancelled
            return null;
        }
        if (validator.test(result)) {
            return result;
        } else {
            throw new Exception(getDescription());
        }
    }

    /**
     * @return true as {@link UserCommand} support being cancelled by the user.
     */
    @Override
    protected boolean canCancel() {
        return true;
    }
}
