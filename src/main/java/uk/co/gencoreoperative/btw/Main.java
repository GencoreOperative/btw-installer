package uk.co.gencoreoperative.btw;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static java.text.MessageFormat.format;
import static uk.co.gencoreoperative.btw.ZipFileSpliterator.*;

// Based on  http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=8925
public class Main {
    private static final String VERSION = "4.A2 Timing Rodent b";

    private static final MineCraftPathResolver FOLDER = new MineCraftPathResolver("/tmp/badger");
    private static final String JSON = "https://bitbucket.org/rwapshott/btw-installer/raw/4f8d1c23e28d34aab7c42aafdcfc6fb52bac9570/static/BetterThanWolves.json";
    private static final String ZIP = "https://bitbucket.org/rwapshott/btw-installer/raw/34ae27fcbde0ae34ed439ef984c33625049828b6/static/BTWMod4-A2TimingRodentb.zip";

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
                .action(folder -> {
                    if (folder.exists()) recursiveDelete(folder);
                    return folder;
                }).validate(f -> !f.exists());

        new ActionBuilder<File, File>()
                .description("create new version")
                .with(FOLDER.oneFiveTwo())
                .action(folder -> {
                    ioOperation(folder, source -> FileUtils.copyDirectory(source, FOLDER.betterThanWolves()));
                    return FOLDER.betterThanWolves();
                })
                .validate(File::exists);

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

        // Open Mod Zip:
        // - for each ZipFileEntry
        // - filter those that are not in the "MINECRAFT-JAR" folder
        // - Read byte[] of ZipFileEntry
        // - Map "relative path" to Object[ ZipEntry, "ByteArrayInputStream" ]
        //
        // Open BetterThanWolves.jar for writing (BTW)
        // Open 1.5.2.jar (152)
        // - for each ZipFileEntry in 152
        // - filter "META-INF/MANIFEST.MF" "META-INF/MOJANG_C.SF" "META-INF/MOJANG_C.DSA"
        // - Check map for an overriding ZipFileEntry
        // - Write selected ZipFileEntry to BTW


//        Map<String, Object[]> map = new HashMap<>();
//        try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream("/Users/robert.wapshott/Dropbox/dev/bitbucket/btw-installer/static/BTWMod4-A2TimingRodentb.zip"))) {
//            ZipEntry entry;
//            while ((entry = inputStream.getNextEntry()) != null) {
//                Object[] zipEntryAndByteStream = {entry, null};
//                map.put(entry.getName(), zipEntryAndByteStream);
//                System.out.println(entry.getName());
//                if (!entry.isDirectory()) {
//                    ByteArrayOutputStream zipContents = new ByteArrayOutputStream();
//                    copyStream(inputStream, zipContents, false, true);
//                    zipEntryAndByteStream[1] = new ByteArrayInputStream(zipContents.toByteArray());
//                    System.out.println("Read " + zipContents.size());
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        streamZip(new File(FOLDER.betterThanWolves(), "1.5.2.jar"))
                .forEach(entry -> System.out.println(entry.entry.getName()));


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

    public static void copyStream(InputStream input, OutputStream output, boolean closeInput, boolean closeOutput) {
        try {
            byte[] buf = new byte[8000];
            int read = 0;
            while (read != -1) {
                read = input.read(buf);
                if (read == -1) continue;
                output.write(buf, 0, read);
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        } finally {
            if (closeInput) ioOperation(input, InputStream::close);
            if (closeOutput) ioOperation(output, OutputStream::close);
        }
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

    private static void unzip(File file, File outputDir) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    InputStream in = zipFile.getInputStream(entry);
                    OutputStream out = new FileOutputStream(entryDestination);
                    IOUtils.copy(in, out);
                    IOUtils.closeQuietly(in);
                    out.close();
                }
            }
        } finally {
            zipFile.close();
        }
    }
}
