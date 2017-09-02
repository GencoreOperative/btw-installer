package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.utils.EntryAndData;
import uk.co.gencoreoperative.btw.utils.FileChooser;
import uk.co.gencoreoperative.btw.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.text.MessageFormat.format;
import static uk.co.gencoreoperative.btw.utils.FileUtils.*;

// Based on  http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=8925
public class Main {
    private static final MineCraftPathResolver FOLDER = new MineCraftPathResolver("/tmp/badger");
    public static final String PATCH_FOLDER = "MINECRAFT-JAR/";

    public static void main(String... args) throws MalformedURLException {
        // Verify that 1.5.2 version is present.
        validate("version 1.5.2 exists", FOLDER.oneFiveTwo(),
                folder -> folder.exists() && new File(folder, "1.5.2.jar").exists());

        File targetFolder = FOLDER.betterThanWolves();
        File targetJson = new File(targetFolder, "BetterThanWolves.json");

        // Remove previous installation
        if (targetFolder.exists()) {
            FileUtils.recursiveDelete(targetFolder);
            validate("previous installation removed", targetFolder, file -> !file.exists());
        }

        // Create target folder
        targetFolder.mkdirs();
        validate("created installation folder", targetFolder, File::exists);

        // Copy JSON from resources - we do not expect this to change
        FileUtils.copyStream(Main.class.getResourceAsStream("/4-A2/BetterThanWolves.json"),
                write(targetJson),
                true, true);
        validate("copy BetterThanWolves.json", targetJson, File::exists);

        File patchFile = FileChooser.requestLocation();
        validate("patch file was selected", patchFile, file -> file != null && file.exists());

        // stream the contents of the BTW Patch utils into a map
        Map<String, EntryAndData> modFiles = new HashMap<>();
        streamZip(patchFile)
                .filter(entry -> entry.getName().startsWith(PATCH_FOLDER))
                .map(entry -> EntryAndData.file(trimModPath(entry.getEntry()), entry.getData()))
                .forEach(entry -> modFiles.put(entry.getName(), entry));

        // Open target jar
        File targetJarFile = new File(targetFolder, "BetterThanWolves.jar");
        try (ZipOutputStream targetStream = openZip(targetJarFile)) {
            // Define function for streaming contents to target jar.
            Consumer<EntryAndData> copyToTargetJar = entry -> {
                try {
                    targetStream.putNextEntry(entry.getEntry());
                    FileUtils.copyStream(entry.getData(), targetStream, true, false);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            };

            // stream the 1.5.2 jar into the target jar, excluding all files that are in the modFiles map
            List<String> metaFilter = Arrays.asList("META-INF/MANIFEST.MF", "META-INF/MOJANG_C.SF", "META-INF/MOJANG_C.DSA");
            streamZip(new File(FOLDER.oneFiveTwo(), "1.5.2.jar"))
                    .filter(EntryAndData::isFile)
                    .filter(entry -> !metaFilter.contains(entry.getName()))
                    .filter(entry -> !modFiles.containsKey(entry.getName()))
                    .forEach(copyToTargetJar);

            // Stream all files from the modFiles map into the target jar.
            modFiles.values().stream()
                    .filter(EntryAndData::isFile)
                    .forEach(copyToTargetJar);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        validate("created BetterThanWolves.jar", targetJarFile, File::exists);


//        // Signal User
    }

    private static ZipEntry trimModPath(ZipEntry entry) {
        String name = entry.getName();
        return new ZipEntry(name.substring(PATCH_FOLDER.length(), name.length()));
    }

    private static <T> void validate(String item, T t, Predicate<T> validate) {
        if (!validate.test(t)) {
            System.err.println(format("✗ {0} failed", item));
            System.exit(-1);
        }
        System.out.println(format("✓ {0}", item));
    }
}
