package uk.co.gencoreoperative.btw.utils;

/**
 * Utility class to aid with calculating presenting percentages for
 * long running tasks.
 */
public class Percentage {
    public static int getProgress(int current, int total) {
        return (int)((current * 100.0f) / total);
    }
}
