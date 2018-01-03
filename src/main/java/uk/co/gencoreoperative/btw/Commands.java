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

/**
 * Defines and wires together all commands that are performed by the installer.
 *
 * Each command represents a single action to be performed either by the user
 * or by the system. Commands can fail if a validation stage does not succeed,
 * or if the user cancels an action.
 */
public class Commands {

    private final Set<AbstractCommand> commands;

    public Commands(ActionFactory actionFactory) {
        // TODO: Split this into two actions.
        AbstractCommand<PathResolver> minecraftHome = new UserCommand<>(
                inputs -> {
                    File path = actionFactory.selectMinecraftHome();
                    if (path == null) {
                        return null;
                    }
                    PathResolver resolver = new PathResolver(path);
                    boolean exists = resolver.oneFiveTwo().exists();
                    if (!exists) {
                        throw new Exception(Errors.MC_ONE_FIVE_TWO_NOT_FOUND.getReason());
                    }
                    return resolver;
                },
                r -> r.versions().exists(),
                "minecraft installation was selected",
                PathResolver.class);

        /*
          Create the target installation folder.
          Depends on the previous installation folder being removed.
          Depends on the user selected the minecraft home folder.
         */
        SystemCommand<TargetFolder> createTargetFolder = new SystemCommand<>(
                inputs -> {
                    PathResolver pathResolver = getInputValue(inputs, PathResolver.class);
                    actionFactory.removePreviousInstallation(pathResolver);
                    return new TargetFolder(actionFactory.createInstallationFolder(pathResolver));
                },
                "created installation folder",
                TargetFolder.class,
                PathResolver.class);

        SystemCommand<File> copyJsonFromResources = new SystemCommand<>(
                inputs -> {
                    TargetFolder targetFolder = getInputValue(inputs, TargetFolder.class);
                    return actionFactory.copyJsonToInstallation(targetFolder.getFolder());
                },
                "copy BetterThanWolves.json",
                null,
                TargetFolder.class);


        UserCommand<PatchFile> requestPatch = new UserCommand<>(
                inputs -> new PatchFile(actionFactory.selectPatchZip()),
                p -> p.getFile().exists(),
                "patch file was selected",
                PatchFile.class);

        SystemCommand<File> assembleMergedJar = new SystemCommand<>(
                inputs -> {
                    PatchFile patchFile = getInputValue(inputs, PatchFile.class);
                    TargetFolder targetFolder = getInputValue(inputs, TargetFolder.class);
                    PathResolver pathResolver = getInputValue(inputs, PathResolver.class);
                    return actionFactory.mergePatchAndRelease(targetFolder.getFolder(), patchFile.getFile(), pathResolver);
                },
                "created BetterThanWolves.jar",
                null,
                PathResolver.class, TargetFolder.class, PatchFile.class);

        commands = new LinkedHashSet<>(Arrays.asList(
                minecraftHome,
                requestPatch,
                createTargetFolder,
                copyJsonFromResources,
                assembleMergedJar));
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

    /**
     * @return An ordered list of commands.
     */
    public Set<AbstractCommand> getCommands() {
        return commands;
    }

    private class TargetFolder {
        private final File folder;

        public TargetFolder(File installationFolder) {
            this.folder = installationFolder;
        }

        public File getFolder() {
            return folder;
        }
    }

    private class PatchFile {
        private final File file;

        public PatchFile(File pathFile) {
            this.file = pathFile;
        }

        public File getFile() {
            return file;
        }
    }
}
