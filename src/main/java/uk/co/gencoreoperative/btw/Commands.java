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
import java.util.function.Predicate;

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
    private final List<Command> commands;

    public Commands(ActionFactory actionFactory) {
        Command<File> installationFolder = new Command<>(
                actionFactory.selectMinecraftHome(),
                EXISTS, // TODO AND is a Directory
                "minecraft installation was selected");

        Command<PathResolver> oneFiveTwo = new Command<>(
                actionFactory.getPathResolver(installationFolder.promise()),
                resolver -> new File(resolver.oneFiveTwo(), "1.5.2.jar").exists(),
                "version 1.5.2 exists");

        Command<File> removePrevious = new Command<>(
                actionFactory.removePreviousInstallation(oneFiveTwo.promise()),
                EXISTS.negate(),
                "previous installation removed");

        Command<File> createTargetFolder = new Command<>(
                actionFactory.createInstallationFolder(oneFiveTwo.promise()),
                EXISTS,
                "created installation folder");

        Command<File> copyJsonFromResources = new Command<>(
                actionFactory.copyJsonToInstallation(createTargetFolder.promise()),
                EXISTS,
                "copy BetterThanWolves.json");

        Command<File> requestPatch = new Command<>(
                actionFactory.selectPatchZip(),
                EXISTS, // TODO: AND is a File
                "patch file was selected");

        Command<File> assembleMergedJar = new Command<>(
                actionFactory.mergePatchAndRelease(createTargetFolder.promise(), requestPatch.promise(), oneFiveTwo.promise()),
                EXISTS, // TODO Validate contents? Validate known CRC?
                "created BetterThanWolves.jar");

        commands = Arrays.asList(
                installationFolder,
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
    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}
