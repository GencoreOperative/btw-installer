package uk.co.gencoreoperative.btw.utils;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple logging placeholder until something more sophisticated
 * can be selected and used.
 * <p>
 * An in-memory logger to collect the limited amount of logging the
 * application will produce during an installation. Specifically
 * aimed at error collection.
 */
public class Logger {
    private final static List<String> lines = new ArrayList<>();

    public static void error(String reason, Exception cause) {
        lines.add(format("Error: {0}\n{1}", reason, cause.getMessage()));
    }

    public static void error(String reason) {
        lines.add(format("Error: {0}\n{1}", reason, "<no cause>"));
    }

    public static void info(String message) {
        lines.add(message);
    }

    public static void info(String message, String... args) {
        lines.add(format(message, (Object[]) args));
    }

    /**
     * Print the lines to the stdout.
     */
    public void printLines() {
        lines.forEach(System.out::println);
    }

    /**
     * @return Non null, possibly empty list of the logged lines.
     */
    public static List<String> getLines() {
        return Collections.unmodifiableList(lines);
    }
}
