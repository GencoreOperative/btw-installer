package uk.co.gencoreoperative.btw.ui.signals;

import java.io.File;

public class InstalledVersion {
    private final File jar;

    public InstalledVersion(File jar) {
        this.jar = jar;
    }

    public File getJar() {
        return jar;
    }
}
