package uk.co.gencoreoperative.btw.ui.signals;

import java.io.File;

public class PatchFile {
    private final File file;

    public PatchFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
