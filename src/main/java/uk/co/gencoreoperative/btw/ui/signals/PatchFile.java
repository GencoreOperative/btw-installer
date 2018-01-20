package uk.co.gencoreoperative.btw.ui.signals;

import java.io.File;

/**
 * The selected BTW Patch file. We model the location of the file, and the
 * detected version of the file this can be determined.
 */
public class PatchFile implements Versioned {
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

    public void setVersion(String version) {
        this.version = version;
    }
}
