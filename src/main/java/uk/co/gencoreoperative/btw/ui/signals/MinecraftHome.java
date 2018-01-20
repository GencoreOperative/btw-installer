package uk.co.gencoreoperative.btw.ui.signals;

import java.io.File;

/**
 * Represents the users Minecraft home location. This might be the system default
 * or a location they have specified themselves.
 */
public class MinecraftHome {
    private final File folder;

    public MinecraftHome(File folder) {
        this.folder = folder;
    }

    public File getFolder() {
        return folder;
    }
}
