package uk.co.gencoreoperative.btw.version;

import java.io.IOException;

/**
 * Defines an implementation that understands how to read and write the version
 * information stored as part of the patch installation.
 * <p>
 * Patch information typically takes the form of the version number of the patch
 * installed, but might also contain other associated information.
 * <p>
 * The rationale behind this is to allow us to automatically upgrade in future
 * versions of the installer if there is a need to do so.
 */
public interface VersionResolver {
    /**
     * Determine if this implementation supports the version information
     * present in the patch folder.
     * <p>
     * In order to support previous versions, each previous implementation
     * will be maintained to ensure that it can read the version information.
     * <p>
     * Implementations should use a deterministic method of working out if
     * the patch installation was performed by their version. For example
     * different file names for the version file.
     *
     * @return {@code true} indicates this resolver is applicable.
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
     *
     * @throws IOException If there was an un-recoverable error whilst
     * reading the version information.
     */
    Version readVersion() throws IOException;

    /**
     * @param version Non null version to store.
     *
     * @throws IOException If there was a non-recoverable error whilst
     * writing the version information.
     */
    void writeVersion(Version version) throws IOException;

    /**
     * Signals to this VersionResolver implementation that it should delete the
     * version information file.
     * <p>
     * This will be signalled at the start of the patching process to remove previous
     * version information from the installation.
     *
     * @throws IOException If there was an un-recoverable error in this process.
     */
    void cleanVersionFile() throws IOException;
}
