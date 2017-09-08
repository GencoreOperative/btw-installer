package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.FileChooser;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.utils.EntryAndData;
import uk.co.gencoreoperative.btw.utils.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static uk.co.gencoreoperative.btw.utils.FileUtils.openZip;
import static uk.co.gencoreoperative.btw.utils.FileUtils.streamZip;
import static uk.co.gencoreoperative.btw.utils.FileUtils.write;

public class TaskFactory {
    private static final String PATCH_FOLDER = "MINECRAFT-JAR/";

    private static final String MINECRAFT_LOCATION = "minecraft.location";
    private static final String PATCH_LOCATION = "patch.location";

    private final DialogFactory dialogFactory;

    public TaskFactory(DialogFactory dialogFactory) {
        this.dialogFactory = dialogFactory;
    }

    public Task<File> selectMinecraftHome() {
        return () -> {
            File previous = FileChooser.getLastOpenedPath(MINECRAFT_LOCATION);
            File selected = dialogFactory.requestFolderLocation(
                    Strings.SELECT_MC_HOME,
                    previous,
                    PathResolver.getDefaultMinecraftPath(),
                    File::isDirectory);
            if (selected != null) {
                FileChooser.setLastOpenedPath(MINECRAFT_LOCATION, selected);
            }
            return selected;
        };
    }

    public Task<PathResolver> getPathResolver(File path) {
        return () -> new PathResolver(path);
    }

    public Task<File> removePreviousInstallation(PathResolver resolver) {
        return () -> {
            File targetFolder = resolver.betterThanWolves();
            if (targetFolder.exists()) {
                FileUtils.recursiveDelete(targetFolder);
            }
            return resolver.betterThanWolves();
        };
    }

    public Task<File> createInstallationFolder(File path) {
        return () -> {
            path.mkdirs();
            return path;
        };
    }

    public Task<File> selectPatchZip() {
        return () -> {
            File previous = FileChooser.getLastOpenedPath(PATCH_LOCATION);
            File selected = dialogFactory.requestFileLocation(
                    Strings.SELECT_ZIP_TITLE,
                    previous,
                    new File(System.getProperty("user.home")),
                    file -> file.getName().toLowerCase().endsWith("zip"));
            if (selected != null) {
                FileChooser.setLastOpenedPath(PATCH_LOCATION, selected);
            }
            return selected;
        };
    }

    public Task<File> copyJsonToInstallation(File folder) {
        return () -> {
            File targetJson = new File(folder, "BetterThanWolves.json");
            FileUtils.copyStream(Main.class.getResourceAsStream("/4-A2/BetterThanWolves.json"),
                    write(targetJson),
                    true, true);
            return targetJson;
        };
    }

    public Task<File> mergePatchAndRelease(File targetFolder, File patchFile, PathResolver pathResolver) {
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
