package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.utils.FileUtils;
import uk.co.gencoreoperative.btw.utils.ZipFileSpliterator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.text.MessageFormat.format;
import static uk.co.gencoreoperative.btw.utils.FileUtils.*;

// Based on  http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=8925
public class Main {
    private static final MineCraftPathResolver FOLDER = new MineCraftPathResolver("/tmp/badger");

    public static void main(String... args) throws MalformedURLException {
        // Verify that 1.5.2 version is present.
        validate("version 1.5.2 exists", FOLDER.oneFiveTwo(), File::exists);

        File targetFolder = FOLDER.betterThanWolves();
        File targetJson = new File(targetFolder, "BetterThanWolves.json");

        // Remove previous installation
        if (targetFolder.exists()) {
            FileUtils.recursiveDelete(targetFolder);
        }
        validate("remove previous installation", targetFolder, File::exists);

        // Copy JSON from resources - we do not expect this to change
        FileUtils.copyStream(Main.class.getResourceAsStream("/4-A2/BetterThanWolves.json"),
                write(targetJson),
                true, true);
        validate("copy BetterThanWolves.json", targetJson, File::exists);

        // Prompt user for Download

        // stream the contents of the BTW Patch utils into a map
        String folder = "MINECRAFT-JAR/";
        Map<String, ZipFileSpliterator.ZipFileEntryAndData> mappedModData = new HashMap<>();
        streamZip(new File("/Users/robert.wapshott/Dropbox/dev/bitbucket/btw-installer/static/BTWMod4-A2TimingRodentb.utils"))
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
        ZipOutputStream targetJar = openZip(new File(targetFolder, "BetterThanWolves.jar"));
        Consumer<ZipFileSpliterator.ZipFileEntryAndData> zipFileEntryAndDataConsumer = entry -> {
            // Check for entry in 'map'
            String name = entry.entry.getName();
            ZipEntry targetEntry = entry.entry;
            ByteArrayInputStream targetStream = entry.data;
            // Write entry to 'target'
            try {
                System.out.println(name);
                targetJar.putNextEntry(targetEntry);
                FileUtils.copyStream(targetStream, targetJar, true, false);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        };

        // stream the 1.5.2 jar into the target jar, excluding all files that are in the patch utils
        List<String> filter = Arrays.asList("META-INF/MANIFEST.MF", "META-INF/MOJANG_C.SF", "META-INF/MOJANG_C.DSA");
        streamZip(new File(FOLDER.oneFiveTwo(), "1.5.2.jar"))
                .filter(entry -> entry.data != null)
                .filter(entry -> !filter.contains(entry.entry.getName()))
                .filter(entry -> !mappedModData.containsKey(entry.entry.getName()))
                .forEach(zipFileEntryAndDataConsumer);

        // Stream all files from the patch utils into the target jar.
        mappedModData.values().stream()
                .filter(entry -> entry.data != null)
                .forEach(zipFileEntryAndDataConsumer);

        try {
            targetJar.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        // Signal User
    }

    private static <T> void validate(String item, T t, Predicate<T> validate) {
        if (!validate.test(t)) {
            System.err.println(format("✗ {0} failed", item));
            System.exit(-1);
        }
        System.out.println(format("✓ {0}", item));
    }
}
