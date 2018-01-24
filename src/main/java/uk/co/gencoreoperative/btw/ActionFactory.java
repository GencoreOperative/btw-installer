package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.FileChooser;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.utils.FileUtils;
import uk.co.gencoreoperative.btw.utils.PathAndData;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.co.gencoreoperative.btw.utils.FileUtils.write;
import static uk.co.gencoreoperative.btw.utils.ZipFileStream.*;

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

    public File selectMinecraftHome() {
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
    }

    public File removePreviousInstallation(PathResolver resolver) {
        File targetFolder = resolver.betterThanWolves();
        if (targetFolder.exists()) {
            FileUtils.recursiveDelete(targetFolder);
        }
        return resolver.betterThanWolves();
    }

    public File createInstallationFolder(PathResolver resolver) {
        File targetFolder = resolver.betterThanWolves();
        targetFolder.mkdirs();
        return targetFolder;
    }

    public File selectPatchZip() {
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
    }

    public File copyJsonToInstallation(File folder) {
        File targetJson = new File(folder, "BetterThanWolves.json");
        FileUtils.copyStream(Main.class.getResourceAsStream("/json/BetterThanWolves.json"),
                write(targetJson),
                true, true);
        return targetJson;
    }

    /**
     * Merge the contents of the source file, with the patch file and store the result in
     * the target file.
     *
     * This operation will stream the source zip file and patch zip file, layering the patch
     * over the source to create the target zip file. If there are duplicate files in the source
     * and patch files, the patch file version will be kept.
     *
     * @param targetZip Target file to write to.
     *
     * @return Non null reference to the target file.
     */
    public File mergePatchAndRelease(Stream<PathAndData> source, Stream<PathAndData> patch, File targetZip) {
        Set<PathAndData> files = patch
                .filter(PathAndData::isFile)
                .collect(Collectors.toSet());

        source.filter(PathAndData::isFile)
                .collect(Collectors.toCollection(() -> files));
        return writeStreamToZipFile(files.stream(), targetZip);
    }

    public File mergeClientJarWithPatch(PathResolver resolver, File patchZip) {
        File source = new File(resolver.oneFiveTwo(), "1.5.2.jar");
        final List<String> excludes = Arrays.asList("META-INF/MANIFEST.MF", "META-INF/MOJANG_C.SF", "META-INF/MOJANG_C.DSA");
        Stream<PathAndData> sourceStream = streamZip(source)
                .filter(p -> !excludes.contains(p.getPath()));

        Stream<PathAndData> patchStream = streamZip(patchZip)
                .filter(p -> p.getPath().startsWith(PATCH_FOLDER))
                .peek(p -> p.setPath(p.getPath().substring(PATCH_FOLDER.length())));

        File target = new File(resolver.betterThanWolves(), "BetterThanWolves.jar");

        return mergePatchAndRelease(sourceStream, patchStream, target);
    }

    public boolean confirmDefaultInstallation() {
        return dialogFactory.confirm(
                Strings.CONFIRM_DEFAULT_MESSAGE.getText(),
                Strings.CONFIRM_DEFAULT_TITLE.getText());
    }
}
