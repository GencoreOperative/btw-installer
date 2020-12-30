package uk.co.gencoreoperative.btw.ui.signals;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class AddonFiles {
    private static final ArrayList<File> addons = new ArrayList<>();
    private static final ArrayList<String> zipPath = new ArrayList<>();

    public static int selectedIndex = -1;

    public void add(File file, String zipPath) {
        addons.add(file);
        AddonFiles.zipPath.add(zipPath);
    }

    public void remove(int index) {
        addons.remove(index);
        zipPath.remove(index);
    }

    public void moveUp(int index) {
        try {
            Collections.swap(addons, index, index - 1);
            Collections.swap(zipPath, index, index - 1);
            selectedIndex--;
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    public void moveDown(int index) {
        try {
            Collections.swap(addons, index, index + 1);
            Collections.swap(zipPath, index, index + 1);
            selectedIndex++;
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    public static ArrayList<File> getAddons() {
        return addons;
    }

    public static File getAddon(int index) {
        return addons.get(index);
    }

    public static String getZipPath(int index) {
        return zipPath.get(index);
    }
}