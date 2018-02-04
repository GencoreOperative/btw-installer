package uk.co.gencoreoperative.btw.version;

import static uk.co.gencoreoperative.btw.utils.ZipFileStream.streamZip;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import uk.co.gencoreoperative.btw.utils.PathAndData;

public class PatchVersionReader {
    private static final String PREFIX = "FlowerChild's Better Than Wolves Total Conversion";

    /**
     * Read the version from the Patch Zip file.
     * <p>
     * This method depends on the format of the Zip file and Readme.txt.
     *
     * @param zip Non null zip file to read.
     * @return {@code null} if there was a problem reading the version string, otherwise non null.
     */
    public String extractVersionFromPatch(File zip) {
        Optional<PathAndData> readme = streamZip(zip)
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
