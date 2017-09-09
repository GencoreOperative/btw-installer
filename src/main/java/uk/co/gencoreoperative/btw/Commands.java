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
 * Assembles all commands that can be performed by the utility and wires them
 * together for use in the utility.
 */
public class Commands {
    private static final Predicate<File> EXISTS = File::exists;
    private final List<Command> commands;

    public Commands(ActionFactory actionFactory) {
        Command<File> installationFolder = new Command<>(
                actionFactory.selectMinecraftHome(),
                EXISTS,
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
                EXISTS,
                "patch file was selected");

        Command<File> assembleMergedJar = new Command<>(
                actionFactory.mergePatchAndRelease(createTargetFolder.promise(), requestPatch.promise(), oneFiveTwo.promise()),
                EXISTS,
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
