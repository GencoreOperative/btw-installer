package uk.co.gencoreoperative.btw.ui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.function.Predicate;
import java.util.prefs.Preferences;

public class FileChooser {
    /**
     * Open a FileChooser to prompt the user to select a specific location.
     *
     * @param key The name of the configuration item being searched for.
     * @return A File indicating the last path selected, or {@code null} if the user cancelled the process.
     */
    public static File requestLocation(Component parent, Strings title, String key, File defaultLocation, Predicate<File> selector) {
        File path = getLastOpenedPath(key);
        if (path == null) path = defaultLocation;

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setCurrentDirectory(path.getParentFile());
        chooser.ensureFileIsVisible(path);
        chooser.setDialogTitle(title.getText());
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return selector.test(f);
            }

            @Override
            public String getDescription() {
                return "Zip Archives";
            }
        });
        int result = chooser.showDialog(parent, Strings.BUTTON_SELECT.getText());
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            setLastOpenedPath(key, selected);
            return selected;
        }
        return null;
    }

    /**
     * Retrieve the last file the user selected for the specific key.
     *
     * @param key The key of the value to be located.
     * @return The last file the user selected, or if is not available, then {@code null}.
     */
    private static File getLastOpenedPath(String key) {
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
    private static void setLastOpenedPath(String key, File path) {
        Preferences.userRoot().put(key, path.getPath());
    }
}
