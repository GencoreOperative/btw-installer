package uk.co.gencoreoperative.btw.actions;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.utils.CheckSumVerifier;
import uk.co.gencoreoperative.btw.utils.Logger;
import uk.co.gencoreoperative.btw.utils.UrlResolver;

/**
 * Locate the 1.5.2 Client Jar based on a file system search of the Minecraft
 * versions folder and the known URL of the download.
 *
 * TODO: Generify to work with server jar
 */
public class Locate {

    // Public download link for the Mojang 1.5.2 client jar.
    private static final String MOJANG_1_5_2 = "https://launcher.mojang.com/mc/game/1.5.2/client/465378c9dc2f779ae1d6e8046ebc46fb53a57968/client.jar";
    // The known filesize of the 1.5.2 client jar
    private static final long MAJONG_1_5_2_FILE_SIZE = 5564661L;

    // MD5 checksum for the 1.5.2 Client Jar
    private static final String MD5_1_5_2 = "6897c3287fb971c9f362eb3ab20f5ddd";

    /**
     * Using two methods, locate the 1.5.2 Minecraft client.
     *
     * First check if there is a copy locally we can use. Second check online in a
     * publicly accessible location for the jar.
     *
     * @param resolver Non null, required for checking the Minecraft installation.
     *
     * @return {@code null} if nether method worked. Otherwise an {@link InputStream} to
     * the client jar.
     *
     * @throws IOException If there was an unrecoverable error encountered during one
     * of the operations.
     */
    public ProgressInputStream locateMinecraftOneFiveTwo(PathResolver resolver,
            ProgressInputStream.ProgressListener listener) throws IOException {
        // Can we find it in the existing deployment?
        InputStream localStream = locateVersion(resolver);

        // Can we download it?
        if (localStream == null) {
            localStream = downloadOneFiveTwo();
        }

        if (localStream == null) return null;
        return new ProgressInputStream(localStream, MAJONG_1_5_2_FILE_SIZE, listener);
    }

    /**
     * Attempt to download the 1.5.2 client jar from the hosted locations provided by Majong.
     *
     * In this case because we do not want to wait for the entire download before returning, we
     * will instead verify the InputStream as we download it.
     *
     * @return {@code null} if there was a problem accessing the URL, e.g. no connection or
     * invalid link.
     */
    private InputStream downloadOneFiveTwo() {
        try {
            InputStream inputStream = new UrlResolver().streamURLContents(new URL(MOJANG_1_5_2));
            Logger.info("Opened stream to 1.5.2 jar from Majong server");
            return CheckSumVerifier.verifiableStream(MD5_1_5_2, inputStream);
        } catch (IOException e) {
            Logger.error("Unable to download 1.5.2 Client Jar", e);
            return null;
        }
    }

    /**
     * Search the {@code minecraft/versions} folder for a file that has both the size and checksum of the
     * 1.5.2 client jar.
     *
     * @param resolver Non null, needed to locate the versions folder.
     *
     * @return {@code null} if a version could not be found or if there was some recoverable error
     * which prevented locating the jar. Otherwise a valid {@link InputStream}.
     *
     * @throws IOException If there was a non-recoverable error reading the Jar or scanning the folders.
     */
    private InputStream locateVersion(PathResolver resolver) throws IOException {
        File folder = resolver.versions();

        if (folder.isFile()) {
            throw new IOException(format(
                    "Could not explore folder '{0}' because it was a file",
                    folder.getAbsolutePath()));
        }
        if (!folder.exists()) {
            return null;
        }

        Optional<File> jar;
        try {
            jar = Files.walk(folder.toPath())
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> f.getName().toLowerCase().endsWith("jar"))
                    .filter(f -> f.length() == MAJONG_1_5_2_FILE_SIZE)
                    .filter(f -> CheckSumVerifier.validateFileStream(MD5_1_5_2, f))
                    .findFirst();
        } catch (IOException e) {
            throw new IOException(format(
                    "Unable to explore the versions folder: {0}",
                    resolver.versions().toString()), e);
        }

        if (jar.isPresent()) {
            Logger.info(format("Located 1.5.2 Jar {0}", jar.get().getPath()));
            try {
                return new FileInputStream(jar.get());
            } catch (FileNotFoundException e) {
                throw new IOException(format(
                        "Unable to read 1.5.2 jar:\n{0}",
                        jar.get().getPath()), e);
            }
        }
        return null;
    }
}
