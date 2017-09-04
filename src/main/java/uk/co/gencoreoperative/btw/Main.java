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

    public static void main(String... args) throws MalformedURLException {
        Progress progress = new Progress();
        Arrays.stream(Tasks.values()).forEach(tasks -> progress.addItem(tasks.getTask()));

        // Request the location of the Minecraft installation
        File installationFolder = FileChooser.requestLocation(
                progress,
                Strings.SELECT_MC_HOME,
                "minecraft.location",
                MineCraftPathResolver.getDefaultMinecraftPath(),
                File::isDirectory);
        // TODO: Handle null response
        validate(Tasks.INSTALLATION_FOLDER, installationFolder, EXISTS);

        MineCraftPathResolver FOLDER = new MineCraftPathResolver(installationFolder);

        // Verify that 1.5.2 version is present.
        // TODO: No action, validate folder and sub file exist
        validate(Tasks.ONE_FIVE_TWO_EXISTS, new File(FOLDER.oneFiveTwo(), "1.5.2.jar"), EXISTS);

        // TODO: Request from user patch location, validate file exists
        File patchFile = FileChooser.requestLocation(
                progress,
                Strings.SELECT_ZIP_TITLE,
                "patch.location",
                new File(System.getProperty("user.home")),
                file -> file.getName().toLowerCase().endsWith("zip"));
        // TODO: Handle null response
        validate(Tasks.PATCH_WAS_SELECTED, patchFile, EXISTS);

        // Remove previous installation
        // TODO: If folder exists delete it - validate folder does not exist
        File targetFolder = FOLDER.betterThanWolves();
        if (targetFolder.exists()) {
            FileUtils.recursiveDelete(targetFolder);
            validate(Tasks.PREVIOUS_REMOVED, targetFolder, EXISTS.negate());
        }

        // Create target folder
        // TODO: If folder did not exist create it - validate folder exists
        targetFolder.mkdirs();
        validate(Tasks.CREATED_FOLDER, targetFolder, File::exists);

        // Copy JSON from resources - we do not expect this to change
        // TODO: Read static resource and write to file - validate file exists
        File targetJson = new File(targetFolder, "BetterThanWolves.json");
        FileUtils.copyStream(Main.class.getResourceAsStream("/4-A2/BetterThanWolves.json"),
                write(targetJson),
                true, true);
        validate(Tasks.COPIED_JSON, targetJson, EXISTS);

        // Open target jar
        // TODO: Create target jar and apply patch - validate file exists
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
        validate(Tasks.COPIED_JAR, targetJarFile, EXISTS);


//        // Signal User
    }

    private static ZipEntry trimModPath(ZipEntry entry) {
        String name = entry.getName();
        return new ZipEntry(name.substring(PATCH_FOLDER.length(), name.length()));
    }

    private static <T> void validate(Tasks item, T t, Predicate<T> validate) {
        if (!validate.test(t)) {
            item.getTask().failed();
            System.exit(-1);
        } else {
            item.getTask().success();
        }
    }
}
