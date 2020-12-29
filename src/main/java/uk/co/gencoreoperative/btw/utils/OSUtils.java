package uk.co.gencoreoperative.btw.utils;

public class OSUtils {

    private OSUtils() {
        // Utility class
    }

    private static final String OS_NAME = System.getProperty("os.name");

    /**
     * @return True if the Windows operating system is detected.
     */
    public static boolean isWindows() {
        return OS_NAME.contains("Windows");
    }

    /**
     * @return True if the MacOS operating system is detected.
     */
    public static boolean isMacOS() {
        return OS_NAME.contains("Mac OS X");
    }

    /**
     * @return True if Linux is the detected operating system.
     */
    public static boolean isLinux() {
        return OS_NAME.toLowerCase().contains("linux");
    }
}
