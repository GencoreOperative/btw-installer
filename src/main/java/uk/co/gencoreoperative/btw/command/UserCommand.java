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
 * correctly.
 */
public class UserCommand<T> extends AbstractCommand<T> {
    private final Supplier<T> action;
    private final Predicate<T> validator;

    public UserCommand(Supplier<T> action, Predicate<T> validator, String description) {
        super(description);
        this.action = action;
        this.validator = validator;
    }

    public Result<T> processAction() {
        action.get();
        return null;
    }
}
