package uk.co.gencoreoperative.btw.utils;

public class OSUtils {
    /**
     * @return True if the Windows operating system is detected.
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }

    /**
     * @return True if the MacOS operating system is detected.
     */
    public static boolean isMacOS() {
        return System.getProperty("os.name").contains("Mac OS X");
    }
}
