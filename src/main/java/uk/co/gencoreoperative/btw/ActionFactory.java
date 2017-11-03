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
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static uk.co.gencoreoperative.btw.utils.FileUtils.openZip;
import static uk.co.gencoreoperative.btw.utils.FileUtils.streamZip;
import static uk.co.gencoreoperative.btw.utils.FileUtils.write;

/**
 * Captures the ability to describe actions that can be performed.
 *
 * This factory makes extensive use of {@link Supplier} to remove the
 * need to calcualte the value being provided to the factory.
 */
public class ActionFactory {
    private static final String PATCH_FOLDER = "MINECRAFT-JAR/";

    private static final String MINECRAFT_LOCATION = "minecraft.location";
    private static final String PATCH_LOCATION = "patch.location";

    private final DialogFactory dialogFactory;

    public ActionFactory(DialogFactory dialogFactory) {
        this.dialogFactory = dialogFactory;
    }

    public Supplier<File> selectMinecraftHome() {
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

    public Supplier<PathResolver> getPathResolver(Supplier<File> path) {
        return () -> new PathResolver(path.get());
    }

    public Supplier<File> removePreviousInstallation(Supplier<PathResolver> resolver) {
        return () -> {
            File targetFolder = resolver.get().betterThanWolves();
            if (targetFolder.exists()) {
                FileUtils.recursiveDelete(targetFolder);
            }
            return resolver.get().betterThanWolves();
        };
    }

    public Supplier<File> createInstallationFolder(Supplier<PathResolver> resolver) {
        return () -> {
            File targetFolder = resolver.get().betterThanWolves();
            targetFolder.mkdirs();
            return targetFolder;
        };
    }

    public Supplier<File> selectPatchZip() {
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

    public Supplier<File> copyJsonToInstallation(Supplier<File> folder) {
        return () -> {
            File targetJson = new File(folder.get(), "BetterThanWolves.json");
            FileUtils.copyStream(Main.class.getResourceAsStream("/4-A2/BetterThanWolves.json"),
                    write(targetJson),
                    true, true);
            return targetJson;
        };
    }

    public Supplier<File> mergePatchAndRelease(Supplier<File> targetFolder, Supplier<File> patchFile, Supplier<PathResolver> pathResolver) {
        return () -> {
            File targetJarFile = new File(targetFolder.get(), "BetterThanWolves.jar");

            // stream the contents of the BTW Patch utils into a map
            Map<String, EntryAndData> modFiles = new HashMap<>();
            streamZip(patchFile.get())
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
                streamZip(new File(pathResolver.get().oneFiveTwo(), "1.5.2.jar"))
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
