package uk.co.gencoreoperative.btw.version;

import static java.text.MessageFormat.*;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;
import uk.co.gencoreoperative.btw.utils.Logger;

/**
 * VersionManager is responsible for reading and writing the version information
 * which is stored with the Patch.
 *
 * The entire role of this process is to both provide the UI with the version
 * information (supports the users understanding of what is being installed) and
 * in the future might allow us to track additional information about the
 * installation, e.g. which mods are installed as well.
 *
 * The VersionManager uses {@link VersionResolver} instances to perform this
 * task. There must be only one {@link VersionResolver} that is both
 * {@link VersionResolver#isApplicable()} and not
 * {@link VersionResolver#isDeprecated()}.
 */
public class VersionManager {
    private final Set<VersionResolver> resolvers;

    public VersionManager(VersionResolver... resolvers) {
        this.resolvers = new HashSet<>(Arrays.asList(resolvers));
        validate();
    }

    /**
     * There must be only one current version format that is active. All others
     * must be deprecated.
     */
    private void validate() {
        Long count = resolvers.stream()
                .filter(r -> !r.isDeprecated())
                .count();
        if (count == 1) return;
        throw new IllegalStateException();
    }

    /**
     * Identify the appropriate version based on the available {@link VersionResolver}
     * instances.
     *
     * @return Optional {@link Version} if the version could be successfully resolved,
     * otherwise the Optional will be empty.
     */
    public Optional<Version> getVersion() {
        Optional<VersionResolver> resolver = resolvers.stream()
                .filter(VersionResolver::isApplicable)
                .findFirst();
        if (!resolver.isPresent()) return Optional.empty();
        try {
            return Optional.of(resolver.get().readVersion());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Write the Version to the patch installation folder.
     *
     * @param version Non null version to write.
     * @throws IOException If there was an un-recoverable error storing the version.
     */
    public void save(Version version) throws IOException {
        VersionResolver resolver = resolvers.stream()
                .filter(r -> !r.isDeprecated())
                .findFirst()
                .orElseThrow(IllegalStateException::new);
        resolver.writeVersion(version);
    }

    /**
     * Initialise the Version object based on the currently selected {@link PatchFile}.
     * <p>
     * During patching we need to initialise the Version, the starting point for this
     * will always be an empty Version with the patch file version string added.
     *
     * @param patchFile The PatchFile being installed is the starting point for
     *                  the version.
     *
     * @return Non null Version.
     */
    public static Version createVersion(PatchFile patchFile) {
        return new Version(patchFile.getPatchVersion());
    }

    /**
     * Prior to installing a new patch in the clients installation folder, we should
     * remove any previous version information.
     * <p>
     * After this method has been invoked there should be no version information in
     * the folder.
     */
    public void cleanVersionInformation() {
        resolvers.stream()
                .filter(VersionResolver::isApplicable)
                .forEach(r -> {
                    try {
                        r.cleanVersionFile();
                    } catch (IOException e) {
                        Logger.error(format(
                                "Clean Version Information for {0} failed",
                                r.getClass().getSimpleName()), e);
                    }
                });
    }

    /**
     * Get an instance of the Version Manager.
     *
     * @param resolver Non null PathResolver needed to identify the installation folder.
     *
     * @return Non null immutable instance of the manager.
     */
    public static VersionManager getVersionManager(PathResolver resolver) {
        return new VersionManager(
                new VersionResolverV1(resolver.betterThanWolves()),
                new VersionResolverV2(resolver.betterThanWolves()));
    }
}
