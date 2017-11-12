/*
 * Copyright 2017 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package uk.co.gencoreoperative.btw;

import static uk.co.gencoreoperative.btw.ui.Errors.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.gencoreoperative.btw.command.AbstractCommand;
import uk.co.gencoreoperative.btw.command.SystemCommand;
import uk.co.gencoreoperative.btw.command.UserCommand;
import uk.co.gencoreoperative.btw.ui.Errors;
import uk.co.gencoreoperative.btw.utils.ThrowingSupplier;

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
                () -> {
                    PathResolver resolver = new PathResolver(actionFactory.selectMinecraftHome());
                    boolean exists = resolver.oneFiveTwo().exists();
                    if (!exists) {
                        throw new Exception(Errors.MC_ONE_FIVE_TWO_NOT_FOUND.getReason());
                    }
                    return resolver;
                },
                r -> r.versions().exists(),
                "minecraft installation was selected");

        SystemCommand<File> removePrevious = new SystemCommand<>(
                () -> {
                    Optional<PathResolver> result = minecraftHome.promise().get();
                    if (!result.isPresent()) {
                        throw new Exception();
                    }
                    return actionFactory.removePreviousInstallation(result.get());
                }, "previous installation removed");

        /**
         * Create the target installation folder.
         * Depends on the previous installation folder being removed.
         * Depends on the user selected the minecraft home folder.
         */
        SystemCommand<File> createTargetFolder = new SystemCommand<>(
                () -> {
                    Optional<File> previous = removePrevious.promise().get();
                    if (!previous.isPresent()) {
                        throw new Exception();
                    }
                    Optional<PathResolver> pathResolver = minecraftHome.promise().get();
                    if (!pathResolver.isPresent()) {
                        throw new Exception();
                    }

                    return actionFactory.createInstallationFolder(pathResolver.get());
                }, "created installation folder");

        SystemCommand<File> copyJsonFromResources = new SystemCommand<>(
                () -> {
                    Optional<File> file = createTargetFolder.promise().get();
                    if (!file.isPresent()) {
                        throw new Exception();
                    }
                    return actionFactory.copyJsonToInstallation(file.get());
                }, "copy BetterThanWolves.json");


        UserCommand<File> requestPatch = new UserCommand<>(
                actionFactory::selectPatchZip, EXISTS, "patch file was selected");

        assembleMergedJar = new SystemCommand<>(
                () -> {
                    Optional<File> targetJson = copyJsonFromResources.promise().get();
                    Optional<File> targetFolder = createTargetFolder.promise().get();
                    Optional<File> patchFile = requestPatch.promise().get();
                    Optional<PathResolver> pathResolver = minecraftHome.promise().get();

                    if (!Stream.of(targetJson, targetFolder, patchFile, pathResolver).allMatch(Optional::isPresent)) {
                        throw new Exception();
                    }

                    return actionFactory.mergePatchAndRelease(targetFolder.get(), patchFile.get(), pathResolver.get());
                }, "created BetterThanWolves.jar");

        commands = Arrays.asList(
                minecraftHome,
                removePrevious,
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
}
