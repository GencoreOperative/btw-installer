/*
 * Copyright 2017 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package uk.co.gencoreoperative.btw.command;

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

    public <E extends Exception> SystemCommand(ThrowingSupplier<T> action, String description) {
        super(description);
        this.action = action;
    }

    /**
     * Process the system action by getting the result of the provided supplier.
     *
     * @return The result T of the supplier if it was successful.
     * @throws Exception If the operation failed.
     */
    @Override
    protected T processAction() throws Exception {
        return action.getOrThrow();
    }
}
