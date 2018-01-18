package uk.co.gencoreoperative.btw.ui.signals;

import java.io.File;

public class PatchFile {
    private final File file;
    private String version;

    public PatchFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getVersion() {
        return version;
    }

    public boolean isVersionSet() {
        return getVersion() != null;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
