package uk.co.gencoreoperative.btw.ui.signals;

import java.io.File;
import java.util.Optional;

import uk.co.gencoreoperative.btw.version.Version;

/**
 * Represents the installed version of Better Than Wolves after this
 * patching utility has completed the installation.
 */
public class InstalledVersion {
    private final File jar;
    private final Version version;

    public InstalledVersion(File jar) {
        this(jar, null);
    }

    public InstalledVersion(File jar, Version version) {
        this.jar = jar;
        this.version = version;
    }

    public File getJar() {
        return jar;
    }

    public Optional<Version> getVersion() {
        if (version == null) {
            return Optional.empty();
        }
        return Optional.of(version);
    }
}
