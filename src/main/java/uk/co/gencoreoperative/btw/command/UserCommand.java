/*
 * Copyright 2017 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package uk.co.gencoreoperative.btw.command;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Describes a command which is completed by user action. This action needs
 * a validation step to verify that the user has performed the action
 * correctly. It also needs to support the concept of being cancelled.
 */
public class UserCommand<T> extends AbstractCommand<T> {
    private final Supplier<T> action;
    private final Predicate<T> validator;

    public UserCommand(Supplier<T> action, Predicate<T> validator, String description) {
        super(description);
        this.action = action;
        this.validator = validator;
    }

    /**
     * Process the user action.
     *
     * @return T if the action was successfully completed, {@code null} if the user cancelled the action.
     * @throws Exception If the validation failed to validate T.
     */
    public T processAction() throws Exception {
        T result = action.get();
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
}
