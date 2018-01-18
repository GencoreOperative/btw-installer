package uk.co.gencoreoperative.btw;

import static uk.co.gencoreoperative.btw.utils.FileUtils.streamZip2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Optional;

import uk.co.gencoreoperative.btw.utils.PathAndData;

/**
 * Responsible for resolving the version of an installed version of
 * Better Than Wolves by this patching utility.
 *
 * We can read the version from a Patch zip readme.txt and this can
 * then be written to the installation folder for re-reading later.
 */
public class VersionResolver {

    private static final String VERSION_TXT = "version.txt";
    private static final String PREFIX = "FlowerChild's Better Than Wolves Total Conversion";

    public String readVersion(File folder) {
        File versionFile = new File(folder, VERSION_TXT);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(versionFile)))) {
            return reader.readLine();
        } catch (IOException ignored) {
        }
        return null;
    }

    public void writeVersion(File folder, String version) {
        File versionFile = new File(folder, VERSION_TXT);
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(versionFile))) {
            writer.println(version);
        } catch (FileNotFoundException ignored) {}
    }

    public String extractVersionFromPatch(File zip) {
        Optional<PathAndData> readme = streamZip2(zip)
                .filter(p -> p.getPath().endsWith("readme.txt"))
                .findFirst();
        if (!readme.isPresent()) return null;

        String first;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(readme.get().getDataStream()))){
            first = reader.readLine();
        } catch (IOException e) {
            return null;
        }

        if (!first.startsWith(PREFIX)) return null;

        return first.substring(PREFIX.length()).trim();
    }
}
