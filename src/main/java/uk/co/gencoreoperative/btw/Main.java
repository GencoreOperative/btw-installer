package uk.co.gencoreoperative.btw;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.text.MessageFormat.format;

public class Main {
    private static final String VERSION = "4.A2 Timing Rodent b";

    private static final MineCraftPathResolver FOLDER = new MineCraftPathResolver("/tmp/badger");
    private static final String JSON = "https://bitbucket.org/rwapshott/btw-installer/raw/4f8d1c23e28d34aab7c42aafdcfc6fb52bac9570/static/BetterThanWolves.json";

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

        // Perform Installation Actions
        new ActionBuilder<File, File>()
                .description("remove previous installation")
                .with(FOLDER.betterThanWolves())
                .action(f -> {
                    if (f.exists()) recursiveDelete(f);
                    return f;
                }).validate(f -> !f.exists());

        new ActionBuilder<File, File>()
                .description("create new version")
                .with(FOLDER.oneFiveTwo())
                .action(f -> {
                    ioOperation(f, folder -> Files.copy(folder.toPath(), FOLDER.betterThanWolves().toPath()));
                    return FOLDER.betterThanWolves();
                })
                .validate(File::exists);

        new ActionBuilder<URL, File>()
                .description(format("download {0} JSON", VERSION))
                .with(new URL(JSON))
                .action(url -> {
                    File target = new File(FOLDER.betterThanWolves(), "BetterThanWolves.json");
                    copyStream(url, target);
                    return target;
                })
                .validate(File::exists);

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

    /**
     * Perform an operation which will generate an IOException. Catch the exception and indicate the failure.
     *
     * @param t
     * @param operation
     * @param <T> The type of the item the operation is being performed on.
     */
    private static <T> void ioOperation(T t, CheckedFunction<T> operation) {
        try {
            operation.apply(t);
        } catch (IOException e) {
            System.err.println(format("✗ operation failed: {0}", e.getMessage()));
        }
    }

    private static void recursiveDelete(File f) {
        ioOperation(f, folder -> Files.walk(folder.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete));
    }

    private static void copyStream(URL url, File target) {
        try {
            Files.copy(url.openStream(), target.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
