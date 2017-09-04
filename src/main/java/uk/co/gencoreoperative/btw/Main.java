package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.ui.FileChooser;
import uk.co.gencoreoperative.btw.ui.Progress;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.utils.EntryAndData;
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

import static uk.co.gencoreoperative.btw.utils.FileUtils.*;

// Based on  http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=8925
public class Main {
    private static final Predicate<File> EXISTS = File::exists;
    private static final String PATCH_FOLDER = "MINECRAFT-JAR/";
    private static Progress progress;

    public static void main(String... args) throws MalformedURLException {
        progress = new Progress();
        Arrays.stream(Tasks.values()).forEach(tasks -> progress.addItem(tasks.getTask()));

        // Request the location of the Minecraft installation
        File installationFolder = selectMinecraftHome()
                .thenValidate(Tasks.INSTALLATION_FOLDER, EXISTS);

        // Verify that 1.5.2 version is present.
        PathResolver pathResolver = getPathResolver(installationFolder)
                .thenValidate(Tasks.ONE_FIVE_TWO_EXISTS,
                        resolver -> new File(resolver.oneFiveTwo(), "1.5.2.jar").exists());

        // Remove previous installation
        File targetFolder = pathResolver.betterThanWolves();
        removePreviousInstallation(pathResolver).thenValidate(Tasks.PREVIOUS_REMOVED, EXISTS.negate());

        // Create target folder
        createInstallationFolder(targetFolder).thenValidate(Tasks.CREATED_FOLDER, EXISTS);

        // Copy JSON from resources - we do not expect this to change
        copyJsonToInstallation(targetFolder).thenValidate(Tasks.COPIED_JSON, EXISTS);

        // Request Patch Zip from user.
        File patchFile = selectPatchZip().thenValidate(Tasks.PATCH_WAS_SELECTED, EXISTS);

        // stream the contents of the BTW Patch utils into a map
        mergePatchAndRelease(targetFolder, patchFile, pathResolver).thenValidate(Tasks.COPIED_JAR, EXISTS);


//        // Signal User
    }

    /**
     * Request the location of the Minecraft installation
     * @return
     */
    private static Task<File> selectMinecraftHome() {
        return () -> FileChooser.requestLocation(
                progress,
                Strings.SELECT_MC_HOME,
                "minecraft.location",
                PathResolver.getDefaultMinecraftPath(),
                File::isDirectory);
    }

    private static Task<PathResolver> getPathResolver(File path) {
        return () -> new PathResolver(path);
    }

    private static Task<File> removePreviousInstallation(PathResolver resolver) {
        return () -> {
            File targetFolder = resolver.betterThanWolves();
            if (targetFolder.exists()) {
                FileUtils.recursiveDelete(targetFolder);
            }
            return resolver.betterThanWolves();
        };
    }

    private static Task<File> createInstallationFolder(File path) {
        return () -> {
            path.mkdirs();
            return path;
        };
    }

    private static Task<File> selectPatchZip() {
        return () -> {
            File patchFile = FileChooser.requestLocation(
                    progress,
                    Strings.SELECT_ZIP_TITLE,
                    "patch.location",
                    new File(System.getProperty("user.home")),
                    file -> file.getName().toLowerCase().endsWith("zip"));
            return patchFile;
        };
    }

    private static Task<File> copyJsonToInstallation(File folder) {
        return () -> {
            File targetJson = new File(folder, "BetterThanWolves.json");
            FileUtils.copyStream(Main.class.getResourceAsStream("/4-A2/BetterThanWolves.json"),
                    write(targetJson),
                    true, true);
            return targetJson;
        };
    }

    private static Task<File> mergePatchAndRelease(File targetFolder, File patchFile, PathResolver pathResolver) {
        return () -> {
            File targetJarFile = new File(targetFolder, "BetterThanWolves.jar");

            // stream the contents of the BTW Patch utils into a map
            Map<String, EntryAndData> modFiles = new HashMap<>();
            streamZip(patchFile)
                    .filter(entry -> entry.getName().startsWith(PATCH_FOLDER))
                    .map(entry -> EntryAndData.file(trimModPath(entry.getEntry()), entry.getData()))
                    .forEach(entry -> modFiles.put(entry.getName(), entry));

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
                streamZip(new File(pathResolver.oneFiveTwo(), "1.5.2.jar"))
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
            return targetJarFile;
        };
    }

    private static ZipEntry trimModPath(ZipEntry entry) {
        String name = entry.getName();
        return new ZipEntry(name.substring(PATCH_FOLDER.length(), name.length()));
    }
}
