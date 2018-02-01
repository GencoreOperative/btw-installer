package uk.co.gencoreoperative.btw.utils;

import java.text.MessageFormat;

/**
 * A simple utility for recording how a section of code takes to
 * execute. Intended for simple metering of code paths rather
 * than metrics.
 */
public class Timer {

    private final long start;
    private final String description;

    private Timer(String description) {
        this.description = description;
        start = System.currentTimeMillis();
    }

    public void stop() {
        System.out.println(MessageFormat.format("{0}: {1}ms", description, System.currentTimeMillis() - start));
    }

    public static Timer time(String description) {
        return new Timer(description);
    }

    public static <T> T timeAndReturn(String description, ReturnAction<T> returnAction) throws Exception {
        Timer timer = new Timer(description);
        try {
            return returnAction.performAction();
        } finally {
            timer.stop();
        }
    }

    public static <T> void time(String description, Action<T> returnAction) throws Exception {
        Timer timer = new Timer(description);
        try {
            returnAction.performAction();
        } finally {
            timer.stop();
        }
    }

    @FunctionalInterface
    public interface ReturnAction<T> {
        T performAction() throws Exception;
    }

    @FunctionalInterface
    public interface Action<T> {
        void performAction() throws Exception;
    }
}
