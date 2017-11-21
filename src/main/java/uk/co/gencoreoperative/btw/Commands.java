package uk.co.gencoreoperative.btw;

import static uk.co.gencoreoperative.btw.utils.ThrowingSupplier.getInputValue;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

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
 *
 * The commands defined have an implicit dependency on prior commands, and as
 * such their order is fixed by the {@link #getCommands()} and
 * {@link #getLastCommand()} methods.
 */
public class Commands {
    // Common validation case
    private static final Predicate<File> EXISTS = File::exists;

    private final List<AbstractCommand> commands;
    private final SystemCommand<File> assembleMergedJar;

    public Commands(ActionFactory actionFactory) {
        AbstractCommand<PathResolver> minecraftHome = new UserCommand<>(
                inputs -> {
                    PathResolver resolver = new PathResolver(actionFactory.selectMinecraftHome());
                    boolean exists = resolver.oneFiveTwo().exists();
                    if (!exists) {
                        throw new Exception(Errors.MC_ONE_FIVE_TWO_NOT_FOUND.getReason());
                    }
                    return resolver;
                },
                r -> r.versions().exists(),
                "minecraft installation was selected",
                PathResolver.class);

        /**
         * Create the target installation folder.
         * Depends on the previous installation folder being removed.
         * Depends on the user selected the minecraft home folder.
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
                    actionFactory.copyJsonToInstallation(targetFolder.getFolder());
                    return null;
                },
                "copy BetterThanWolves.json",
                null,
                TargetFolder.class);


        UserCommand<File> requestPatch = new UserCommand<>(
                inputs -> actionFactory.selectPatchZip(), EXISTS,
                "patch file was selected",
                PatchFile.class);

        assembleMergedJar = new SystemCommand<>(
                inputs -> {
                    PatchFile patchFile = getInputValue(inputs, PatchFile.class);
                    TargetFolder targetFolder = getInputValue(inputs, TargetFolder.class);
                    PathResolver pathResolver = getInputValue(inputs, PathResolver.class);
                    return actionFactory.mergePatchAndRelease(targetFolder.getFolder(), patchFile.getFile(), pathResolver);
                },
                "created BetterThanWolves.jar",
                null,
                PathResolver.class, TargetFolder.class, PatchFile.class);

        commands = Arrays.asList(
                minecraftHome,
                createTargetFolder,
                copyJsonFromResources,
                requestPatch,
                assembleMergedJar);
    }

    /**
     * @return An ordered list of commands. The relationship between commands
     * should hopefully reflect this order.
     */
    public List<AbstractCommand> getCommands() {
        return commands;
    }

    /**
     * The last command will be the one at the bottom of the chain. When this command
     * is executed it will trigger the chain of previous commands to execute.
     *
     * @return A single command which can act as the entry point for processing.
     */
    public AbstractCommand<File> getLastCommand() {
        return assembleMergedJar;
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
