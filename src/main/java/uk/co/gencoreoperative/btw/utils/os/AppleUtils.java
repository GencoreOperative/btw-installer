package uk.co.gencoreoperative.btw.utils.os;

import com.apple.eawt.Application;
import org.jetbrains.annotations.NotNull;

import java.awt.Image;

/**
 * MacOS specific user interface configuration and customisation.
 */
public class AppleUtils {
    /**
     * Set the title of the MacOS UI.
     * <p>
     * See <a href="https://stackoverflow.com/questions/8918826/java-os-x-lion-set-application-name-doesnt-work">
     *     Java OS X Lion Set application name doesn't work</a> for more details.
     *
     * @param name Non null.
     */
    public static void setTitle(@NotNull String name) {
        System.setProperty("apple.awt.application.name", name);
    }

    /**
     * Assign the MacOS Dock icon image.
     *
     * @param icn Non null.
     */
    public static void setDockIcon(@NotNull Image icn) {
        Application.getApplication().setDockIconImage(icn);
    }
}
