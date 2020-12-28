package uk.co.gencoreoperative.btw.utils;

import java.awt.Image;

public interface OSUtils {

    String OS_NAME = System.getProperty("os.name");

    /**
     * @return True if the Windows operating system is detected.
     */
    static boolean isWindows() {
        return OS_NAME.contains("Windows");
    }

    /**
     * @return True if the MacOS operating system is detected.
     */
    static boolean isMacOS() {
        return OS_NAME.contains("Mac OS X");
    }

    /**
     * @return True if Linux is the detected operating system.
     */
    static boolean isLinux() {
        return OS_NAME.toLowerCase().contains("linux");
    }

    void setIcon(Image icn );
}
