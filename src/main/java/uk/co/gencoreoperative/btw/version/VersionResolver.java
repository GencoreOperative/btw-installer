package uk.co.gencoreoperative.btw.version;

import java.io.IOException;

/**
 * Defines an implementation that understands how to read and write the version
 * information stored as part of the patch installation.
 */
public interface VersionResolver {
    /**
     * In order to support the versions of the installer that
     * are already in use, we need to identify this resolver
     * created the version information.
     *
     * @return True indicates this resolver is applicable.
     */
    boolean isApplicable();

    /**
     * @return {@code true} indicates that this VersionResolver is now
     * deprecated and that the {@link VersionManager} should not select
     * it when writing.
     */
    boolean isDeprecated();

    /**
     * If the resolver implementation indicates true on the
     * {@link #isApplicable()} method, then this method will
     * be called to read the version.
     *
     * @return Non null version, read from the installation.
     */
    Version readVersion() throws IOException;

    /**
     * @param version Non null version to store.
     */
    void writeVersion(Version version) throws IOException;
}
