package uk.co.gencoreoperative.btw;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.text.MessageFormat.format;

public class Main {

    private static final MineCraftPathResolver FOLDER = new MineCraftPathResolver("/tmp/badger");
    // Check 1.5.2 versions
    // Check internet connection
    // Check "jar" on command line
    private static List<Check> prerequisites = Arrays.asList(
            new FileCheck(FOLDER),
            new FileCheck(FOLDER.oneFiveTwo()));

    public static void main(String... args) throws MalformedURLException {
        // Check all prerequisite conditions are met
        final boolean[] failed = {false};
        prerequisites.forEach(c -> {
            boolean check = c.check();
            if (!check) {
                failed[0] = true;
            }
            System.out.println(format("{0} {1}", check ? "✓" : "✗", c.item()));
        });

        if (failed[0]) {
            System.exit(-1);
        }

        // Perform installation.


        // Remove previous BTW installation
        // - If folder present, delete
        // -- Validate folder not present
        new ActionBuilder<File, File>()
                .description("remove previous installation")
                .with(FOLDER.betterThanWolves())
                .action(f -> {
                    if (f.exists()) recursiveDelete(f);
                    return f;
                }).validate(f -> !f.exists());

        // Copy 1.5.2 to BetterThanWolves
        // - Copy folder to target
        // -- Validate folder present
        new ActionBuilder<File, File>()
                .description("create new version")
                .with(FOLDER.oneFiveTwo())
                .action(f -> {
                    fileOperation(f, folder -> Files.copy(folder.toPath(), FOLDER.betterThanWolves().toPath()));
                    return f;
                })
                .validate(File::exists);
//
//        // Download JSON file
//        // - Download file from GIT to target
//        // -- Validate file present
//        action("download version JSON",
//                new URL("http://"),
//                s -> {
//                    // download the file, stream to target
//                    // new File(FOLDER.oneFiveTwo(), "BetterThanWolves.json");
//                    return new File(FOLDER.oneFiveTwo(), "BetterThanWolves.json");
//                }, File::exists);
//
//        // Download latest BTW installation
//        // - Download file from Website or GIT
//        // -- Validate file present
//
//        // Unpack zip and update Jar
//        // - Stream zip contents, merging into Jar
//        // -- Validate jar somehow (size increase, Jar test function)
//
//        // Update Jar META-INF
//        // - Delete META-INF from jar
//        // -- Validate jar
//
//        // Signal User
    }

    private static void fileOperation(File file, CheckedFunction<File> operation) {
        try {
            operation.apply(file);
        } catch (IOException e) {
            System.err.println(MessageFormat.format("✗ operation failed: {0}", e.getMessage()));
        }
    }

    private static void recursiveDelete(File f) {
        fileOperation(f, folder -> Files.walk(folder.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete));
    }

    private static void folderCopy(File folder) {
        try {
            Files.copy(folder.toPath(), FOLDER.betterThanWolves().toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
