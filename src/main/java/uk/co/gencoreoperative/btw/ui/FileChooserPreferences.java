package uk.co.gencoreoperative.btw.ui;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Provides the ability to recall the last selected location for a
 * requested key.
 */
public class FileChooserPreferences {
    /**
     * Retrieve the last file the user selected for the specific key.
     *
     * @param key The key of the value to be located.
     * @return The last file the user selected, or if is not available, then {@code null}.
     */
    public static File getLastOpenedPath(String key) {
        String lastOpened = Preferences.userRoot().get(key, null);
        if (lastOpened == null) return null;
        File path = new File(lastOpened);
        if (!path.exists()) return null;
        return path;
    }

    /**
     * Store the last opened path for the specific key.
     *
     * @param key The key of the value to store.
     * @param path The file that was selected by the user.
     */
    public static void setLastOpenedPath(String key, File path) {
        Preferences.userRoot().put(key, path.getPath());
    }
}
