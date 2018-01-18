package uk.co.gencoreoperative.btw.ui.signals;

import java.io.File;

/**
 * Represents the installed version of Better Than Wolves after this
 * patching utility has completed the installation.
 */
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
