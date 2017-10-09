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
 * Describes a command which is performed by the system and might typically
 * involve file system operations. This command can fail for a known reason
 * and if this is the case an exception will capture the detail.
 */
public class SystemCommand<T> extends AbstractCommand<T> {
    private ThrowingSupplier<T, ? extends Exception> action;
    public <E extends Exception> SystemCommand(ThrowingSupplier<T, E> action, String description) {
        super(description);
        this.action = action;
    }

    @Override
    public T process() {
        try {
            action.getOrThrow();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
