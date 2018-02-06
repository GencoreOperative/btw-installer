package uk.co.gencoreoperative.btw.version;

import static java.text.MessageFormat.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Responsible for resolving the version of an installed version of
 * Better Than Wolves by this patching utility.
 *
 * We can read the version from a Patch zip readme.txt and this can
 * then be written to the installation folder for re-reading later.
 */
public class VersionResolverV1 implements VersionResolver {

    private static final String VERSION_TXT = "version.txt";
    private final File versionFile;

    public VersionResolverV1(File folder) {
        versionFile = new File(folder, VERSION_TXT);
    }

    @Override
    public boolean isApplicable() {
        return versionFile.exists();
    }

    @Override
    public boolean isDeprecated() {
        return true;
    }

    public Version readVersion() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(versionFile)))) {
            return new Version(reader.readLine());
        }
    }

    @Override
    public void writeVersion(Version version) throws IOException {
        throw new UnsupportedOperationException("V1 not supported");
    }

    @Override
    public void cleanVersionFile() throws IOException {
        if (!versionFile.delete()) {
            throw new IOException(format("Failed to delete version file {0}", versionFile.getPath()));
        }
    }

    // Test Only
    static void writeVersion(File folder, String version) throws IOException {
        File versionFile = new File(folder, VERSION_TXT);
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(versionFile))) {
            writer.println(version);
        } catch (FileNotFoundException e) {
            throw new IOException("Failed to write version", e);
        }
    }
}
