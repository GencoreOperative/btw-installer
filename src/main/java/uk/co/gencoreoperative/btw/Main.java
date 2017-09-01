package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.zip.FileUtils;
import uk.co.gencoreoperative.btw.zip.ZipFileSpliterator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.text.MessageFormat.format;
import static uk.co.gencoreoperative.btw.zip.FileUtils.*;

// Based on  http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=8925
public class Main {
    private static final String VERSION = "4.A2 Timing Rodent b";

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

        // Remove previous installation
        File targetFolder = FOLDER.betterThanWolves();
        if (targetFolder.exists()) {
            recursiveDelete(targetFolder);
        }
        validate("remove previous installation", targetFolder, File::exists);

        // Copy JSON from resources - we do not expect this to change
        File targetJson = new File(FOLDER.betterThanWolves(), "BetterThanWolves.json");
        FileUtils.copyStream(Main.class.getResourceAsStream("/4-A2/BetterThanWolves.json"),
                write(targetJson),
                true, true);
        validate("copy BetterThanWolves.json", targetJson, File::exists);

//        new ActionBuilder<URL, File>()
//                .description(format("download {0} zip", VERSION))
//                .with(new URL(ZIP))
//                .action(url -> {
//                    File target = new File(FOLDER.betterThanWolves(), "BetterThanWolves.zip");
//                    copyStream(url, target);
//                    return target;
//                })
//                .validate(File::exists);

//        new ActionBuilder<URL, File>()
//                .description(format("download {0} JSON", VERSION))
//                .with(new URL(JSON))
//                .action(url -> {
//                    File target = new File(FOLDER.betterThanWolves(), "BetterThanWolves.json");
//                    copyStream(url, target);
//                    return target;
//                })
//                .validate(File::exists);


        // stream the contents of the BTW Patch zip into a map
        String folder = "MINECRAFT-JAR/";
        Map<String, ZipFileSpliterator.ZipFileEntryAndData> mappedModData = new HashMap<>();
        streamZip(new File("/Users/robert.wapshott/Dropbox/dev/bitbucket/btw-installer/static/BTWMod4-A2TimingRodentb.zip"))
                .filter(entry -> entry.entry.getName().startsWith(folder))
                .map(entry -> {
                    ZipEntry current = entry.entry;
                    String name = current.getName();
                    ZipEntry updated = new ZipEntry(name.substring(folder.length(), name.length()));
                    entry.entry = updated;
                    return entry;
                })
                .forEach(entry -> mappedModData.put(entry.entry.getName(), entry));

        // Open target Zip file
        ZipOutputStream target = openZip(new File(FOLDER.betterThanWolves(), "BetterThanWolves.jar"));

        Consumer<ZipFileSpliterator.ZipFileEntryAndData> zipFileEntryAndDataConsumer = entry -> {
            // Check for entry in 'map'
            String name = entry.entry.getName();
            ZipEntry targetEntry = entry.entry;
            ByteArrayInputStream targetStream = entry.data;
            // Write entry to 'target'
            try {
                System.out.println(name);
                target.putNextEntry(targetEntry);
                FileUtils.copyStream(targetStream, target, true, false);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        };

        // stream the 1.5.2 jar into the target jar, excluding all files that are in the patch zip
        List<String> filter = Arrays.asList("META-INF/MANIFEST.MF", "META-INF/MOJANG_C.SF", "META-INF/MOJANG_C.DSA");
        streamZip(new File(FOLDER.oneFiveTwo(), "1.5.2.jar"))
                .filter(entry -> entry.data != null)
                .filter(entry -> !filter.contains(entry.entry.getName()))
                .filter(entry -> !mappedModData.containsKey(entry.entry.getName()))
                .forEach(zipFileEntryAndDataConsumer);

        // Stream all files from the patch zip into the target jar.
        mappedModData.values().stream()
                .filter(entry -> entry.data != null)
                .forEach(zipFileEntryAndDataConsumer);

        try {
            target.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//
//        // Signal User
    }

    private static <T> void validate(String item, T t, Predicate<T> validate) {
        if (!validate.test(t)) {
            System.err.println(format("✗ {0} failed", item));
            System.exit(-1);
        }
        System.out.println(format("✓ {0}", item));
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
}
