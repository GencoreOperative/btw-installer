package uk.co.gencoreoperative.btw;

import static java.text.MessageFormat.format;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import uk.co.gencoreoperative.btw.command.AbstractCommand;
import uk.co.gencoreoperative.btw.command.SystemCommand;
import uk.co.gencoreoperative.btw.command.UserCommand;
import uk.co.gencoreoperative.btw.ui.Errors;
import uk.co.gencoreoperative.btw.utils.FileUtils;

/**
 * Defines and wires together all commands that are performed by the installer.
 *
 * Each command represents a single action to be performed either by the user
 * or by the system. Commands can fail if a validation stage does not succeed,
 * or if the user cancels an action.
 */
public class Commands {
    private ActionFactory actionFactory;

    private AbstractCommand<PathResolver> detectMinecraftHome = new UserCommand<>(
            inputs -> {
                PathResolver resolver = new PathResolver();
                if (resolver.get().isDirectory() && actionFactory.confirmDefaultInstallation()) {
                    return resolver;
                }

                // Otherwise ask for the installation location
                File path = actionFactory.selectMinecraftHome();
                if (path == null) {
                    return null;
                }
                return new PathResolver(path);
            },
            r -> r.get().exists(),
            "Use default Minecraft installation",
            PathResolver.class);


    public Commands(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    /**
     * Get all commands needed for client installation.
     * @return An ordered non null non empty set.
     */
    public Set<AbstractCommand> getClientCommands() {
        AbstractCommand<Void> verifyOneFiveTwo = new SystemCommand<>(
                inputs -> {
                    PathResolver resolver = getInputValue(inputs, PathResolver.class);
                    if (!resolver.oneFiveTwo().exists()) {
                        String error = format("{0} in folder\n{1}",
                                Errors.MC_ONE_FIVE_TWO_NOT_FOUND.getReason(),
                                resolver.versions().getAbsolutePath());
                        throw new Exception(error);
                    }
                    return null;
                },
                "verify version 1.5.2 exists",
                null,
                PathResolver.class);

        /*
          Create the target installation folder.
          Depends on the previous installation folder being removed.
          Depends on the user selected the minecraft home folder.
         */
        SystemCommand<TargetFolder> createTargetFolder = new SystemCommand<>(
                inputs -> {
                    PathResolver resolver = getInputValue(inputs, PathResolver.class);
                    actionFactory.removePreviousInstallation(resolver);
                    return new TargetFolder(actionFactory.createInstallationFolder(resolver));
                },
                "created installation folder",
                TargetFolder.class,
                PathResolver.class);

        SystemCommand<File> copyJsonFromResources = new SystemCommand<>(
                inputs -> {
                    TargetFolder targetFolder = getInputValue(inputs, TargetFolder.class);
                    return actionFactory.copyJsonToInstallation(targetFolder.folder);
                },
                "copy BetterThanWolves.json",
                null,
                TargetFolder.class);


        UserCommand<PatchFile> requestPatch = new UserCommand<>(
                inputs -> {
                    File pathFile = actionFactory.selectPatchZip();
                    if (pathFile == null) return null;
                    return new PatchFile(pathFile);
                },
                p -> p.file.exists() && p.file.isFile(),
                "patch file was selected",
                PatchFile.class);

        SystemCommand<File> assembleMergedJar = new SystemCommand<>(
                inputs -> {
                    PathResolver resolver = getInputValue(inputs, PathResolver.class);
                    PatchFile patchFile = getInputValue(inputs, PatchFile.class);
                    return actionFactory.mergeClientJarWithPatch(resolver, patchFile.file);
                },
                "created BetterThanWolves.jar",
                null,
                PathResolver.class, TargetFolder.class, PatchFile.class);

        return new LinkedHashSet<>(Arrays.asList(
                detectMinecraftHome,
                verifyOneFiveTwo,
                requestPatch,
                createTargetFolder,
                copyJsonFromResources,
                assembleMergedJar));
    }

    /**
     * A series of commands that will remove the BetterThanWolves version from the clients
     * installation.
     *
     * @return Non null ordered set of {@link AbstractCommand}.
     */
    public Set<AbstractCommand> getClientRemoveCommands() {
        AbstractCommand<BTWPath> verifyBTW = new SystemCommand<>(
                inputs -> {
                    PathResolver resolver = getInputValue(inputs, PathResolver.class);
                    if (!resolver.betterThanWolves().exists()) {
                        throw new Exception(Errors.BTW_RELEASE_NOT_FOUND.getReason());
                    }
                    return new BTWPath(resolver.betterThanWolves());
                },
                "Verify BetterThanWolves version exists",
                BTWPath.class,
                PathResolver.class);

        AbstractCommand<Void> removeFolder = new SystemCommand<>(
                inputs -> {
                    BTWPath path = getInputValue(inputs, BTWPath.class);
                    FileUtils.recursiveDelete(path.file);
                    return null;
                },
                "Remove Better Than Wolves version",
                null,
                BTWPath.class);

        return new LinkedHashSet<>(Arrays.asList(
                detectMinecraftHome,
                verifyBTW,
                removeFolder));
    }

    /**
     * Given a Map of inputs and the requested class, find the value that corresponds to the
     * requested class.
     *
     * @param inputs Non null, possibly empty
     * @param clazz Non null
     * @param <T> The type of the return value (complete with unchecked casting)
     * @return Possibly null return value of type T
     */
    @SuppressWarnings("unchecked")
    private static <T> T getInputValue(Map<Class, Object> inputs, Class<T> clazz) throws Exception {
        Object o = inputs.get(clazz);
        try {
            return (T) o;
        } catch (ClassCastException e) {
            throw new Exception(format(
                    "Failed to cast value class {0} to type {1}",
                    o.getClass(), clazz));
        }

    }

    private class TargetFolder {
        public final File folder;
        public TargetFolder(File installationFolder) {
            this.folder = installationFolder;
        }
    }

    private class PatchFile {
        public final File file;
        public PatchFile(File pathFile) {
            this.file = pathFile;
        }
    }

    private class MinecraftPath {
        public final File file;
        private MinecraftPath(File file) {
            this.file = file;
        }
    }

    private class BTWPath {
        public final File file;
        private BTWPath(File file) {
            this.file = file;
        }
    }
}
