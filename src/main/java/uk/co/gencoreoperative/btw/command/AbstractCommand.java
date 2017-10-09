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

/**
 * Describes a generic command which can be processed. It has the states:
 * - Initial
 * - Success
 * - Failed (reason)
 */
public abstract class AbstractCommand<T> {
    private final String description;

    private final AtomicBoolean success = new AtomicBoolean();
    private final AtomicBoolean processed  = new AtomicBoolean(false);
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicReference<T> result = new AtomicReference<>();

    public AbstractCommand(String description) {
        this.description = description;
    }

    public void process() {
        Result<T> result = processAction();
        processed.set(true);
        if (result == null) {
            cancelled.set(true);
        } else {
            success.set(true);
        }
    }

    protected abstract Result<T> processAction();

    protected class Result<T> {
        private final String reason;
        private T result;
        public Result(T result) {
            this.result = result;
            this.reason = null;
        }

        public Result(String error) {
            this.result = null;
            this.reason = error;
        }
    }
}
