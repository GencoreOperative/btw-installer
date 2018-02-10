package uk.co.gencoreoperative.btw.utils;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OSUtils {

    public static final String OS_NAME = System.getProperty("os.name");

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

    public static void setIcon(Image icn ) {
        try {
            Class<?> aClass = Class.forName("com.apple.eawt.Application", false, null);
            aClass.getDeclaredMethod("setDockIconImage", Image.class).invoke(aClass.newInstance(), icn);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Logger.error("Failed to set the MacOS Doc Icon", e);
            return;
        }
    }
}
