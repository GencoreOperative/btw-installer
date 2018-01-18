package uk.co.gencoreoperative.btw.ui.signals;

import java.io.File;

public class InstalledVersion {
    private final File jar;
    private String version;

    public InstalledVersion(File jar) {
        this.jar = jar;
    }

    public File getJar() {
        return jar;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
