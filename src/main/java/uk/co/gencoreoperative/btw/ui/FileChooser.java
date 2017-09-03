package uk.co.gencoreoperative.btw.ui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.prefs.Preferences;

public class FileChooser {
    public static File requestLocation() {
        File path = getLastOpenedPath();
        JFileChooser chooser = new JFileChooser(path);
        chooser.setDialogTitle("Select BetterThanWolves Zip");
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith("zip");
            }

            @Override
            public String getDescription() {
                return "Zip Archives";
            }
        });
        int result = chooser.showDialog(null, "Open");
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            setLastOpenedPath(selected);
            return selected;
        }
        return null;
    }

    /**
     * @return The last file the user selected, or if it has moved or been deleted then the users home folder.
     */
    private static File getLastOpenedPath() {
        File defaultPath = new File(System.getProperty("user.home"));
        String lastOpened = Preferences.userRoot().get("last_opened", null);
        if (lastOpened == null) return defaultPath;
        File path = new File(lastOpened);
        if (!path.exists()) return defaultPath;
        return path;
    }

    /**
     * @param path The file that was selected by the user.
     */
    private static void setLastOpenedPath(File path) {
        Preferences.userRoot().put("last_opened", path.getPath());
    }
}
