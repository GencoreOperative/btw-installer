/*
 * Copyright 2017 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package uk.co.gencoreoperative.btw;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import uk.co.gencoreoperative.btw.command.AbstractCommand;
import uk.co.gencoreoperative.btw.command.SystemCommand;
import uk.co.gencoreoperative.btw.command.UserCommand;
import uk.co.gencoreoperative.btw.utils.ThrowingSupplier;

/**
 * Defines and wires together all commands that are performed by the installer.
 *
 * The commands created have an implicit dependency on prior commands, and as
 * such their order is fixed by the {@link #getCommands()} method.
 *
 * // TODO: Two types of failure - user error, and operating system error.
 * // User error is a genuine case that needs validation
 * // OS error is generally caught by exceptions - the failure can be tracked
 */
public class Commands {
    private static final Predicate<File> EXISTS = File::exists;
    private final List<AbstractCommand> commands;
    private final SystemCommand<File> assembleMergedJar;

    public Commands(ActionFactory actionFactory) {
        AbstractCommand<File> minecraftHome = new UserCommand<>(
                actionFactory::selectMinecraftHome,
                EXISTS.and(File::isDirectory),
                "minecraft installation was selected");

        // actionFactory.getPathResolver(installationFolder.promise()),
        // resolver -> new File(resolver.oneFiveTwo(), "1.5.2.jar").exists(),
        SystemCommand<PathResolver> oneFiveTwo = new SystemCommand<>(
                () -> {
                    Optional<File> result = minecraftHome.promise().get();
                    if (!result.isPresent()) {
                        throw new Exception();
                    }
                    return actionFactory.getPathResolver(result.get());
                }, "version 1.5.2 exists");

        SystemCommand<File> removePrevious = new SystemCommand<>(
                () -> {
                    Optional<PathResolver> result = oneFiveTwo.promise().get();
                    if (!result.isPresent()) {
                        throw new Exception();
                    }
                    return actionFactory.removePreviousInstallation(result.get());
                }, "previous installation removed");

        SystemCommand<File> createTargetFolder = new SystemCommand<>(
                () -> {
                    Optional<PathResolver> pathResolver = oneFiveTwo.promise().get();
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
                    Optional<File> targetFolder = createTargetFolder.promise().get();
                    Optional<File> patchFile = requestPatch.promise().get();
                    Optional<PathResolver> pathResolver = oneFiveTwo.promise().get();
                    if (!targetFolder.isPresent() || !patchFile.isPresent() || !pathResolver.isPresent()) {
                        throw new Exception();
                    }

                    return actionFactory.mergePatchAndRelease(targetFolder.get(), patchFile.get(), pathResolver.get());
                }, "created BetterThanWolves.jar");

        commands = Arrays.asList(
                minecraftHome,
                oneFiveTwo,
                removePrevious,
                createTargetFolder,
                copyJsonFromResources,
                requestPatch,
                assembleMergedJar);
    }

    /**
     * @return An ordered list of commands to be executed.
     */
    public List<AbstractCommand> getCommands() {
        return commands;
    }

    public AbstractCommand getLastCommand() {
        return assembleMergedJar;
    }
}
