package uk.co.gencoreoperative.btw.utils;

public class OSUtils {
    public static boolean isWindows() {
        String operatingSystem = System.getProperty("os.name");
        return !"Mac OS X".equalsIgnoreCase(operatingSystem);
    }
}
