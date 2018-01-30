package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.FileChooser;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.utils.FileUtils;
import uk.co.gencoreoperative.btw.utils.PathAndData;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.co.gencoreoperative.btw.utils.FileUtils.write;
import static uk.co.gencoreoperative.btw.utils.ZipFileStream.*;

import javax.swing.*;

/**
 * Captures the ability to describe actions that can be performed.
 *
 * This factory makes extensive use of {@link Supplier} to remove the
 * need to calcualte the value being provided to the factory.
 */
public class ActionFactory {
    private static final String PATCH_FOLDER = "MINECRAFT-JAR/";

    public ActionFactory() {
    }

    // TODO: Fold into FileUtils, test and indicate error
    public File removePreviousInstallation(PathResolver resolver) {
        File targetFolder = resolver.betterThanWolves();
        if (targetFolder.exists()) {
            FileUtils.recursiveDelete(targetFolder);
        }
        return resolver.betterThanWolves();
    }

    // TODO: Fold into FileUtils, test and indicate error
    public File createInstallationFolder(PathResolver resolver) {
        File targetFolder = resolver.betterThanWolves();
        targetFolder.mkdirs();
        return targetFolder;
    }

    public File copyJsonToInstallation(File folder) {
        File targetJson = new File(folder, "BetterThanWolves.json");
        FileUtils.copyStream(ActionFactory.class.getResourceAsStream("/json/BetterThanWolves.json"),
                write(targetJson),
                true, true);
        return targetJson;
    }

    public Stream<PathAndData> streamClientJar(PathResolver resolver) {
        File source = new File(resolver.oneFiveTwo(), "1.5.2.jar");
        final List<String> excludes = Arrays.asList("META-INF/MANIFEST.MF", "META-INF/MOJANG_C.SF", "META-INF/MOJANG_C.DSA");
        return streamZip(source)
                .filter(PathAndData::isFile)
                .filter(p -> !excludes.contains(p.getPath()));
    }

    public Stream<PathAndData> streamPatchZip(File patchZip) {
        return streamZip(patchZip)
                .filter(PathAndData::isFile)
                .filter(p -> p.getPath().startsWith(PATCH_FOLDER))
                .peek(p -> p.setPath(p.getPath().substring(PATCH_FOLDER.length())));
    }

    /**
     * Places the 'first' stream into a set, and then applies the 'second' stream
     * to this. The set acts as a de-duplication mechanism using object equality.
     * @param first non null, possibly empty.
     * @param second non null, possibly empty.
     * @return Non null, possible empty set of results of type T.
     */
    public <T> Set<T> removeDuplicates(Stream<T> first, Stream<T> second) {
        Set<T> set = first.collect(Collectors.toSet());
        return second.collect(Collectors.toCollection(() -> set));
    }

    public MonitoredSet mergeClientWithPatch(PathResolver resolver, File patchZip) {
        Stream<PathAndData> client = streamClientJar(resolver);
        Stream<PathAndData> patch = streamPatchZip(patchZip);
        return new MonitoredSet(removeDuplicates(patch, client));
    }

    public File writeToTarget(PathResolver resolver, MonitoredSet monitoredSet) {
        File target = new File(resolver.betterThanWolves(), "BetterThanWolves.jar");
        return writeStreamToZipFile(monitoredSet.stream(), target);
    }

    public static class MonitoredSet extends Observable {
        private final Set<PathAndData> set;
        private final long total;
        private AtomicLong current = new AtomicLong(0);
        public MonitoredSet(Set<PathAndData> set) {
            super();
            this.set = set;
            total = set.stream().mapToLong(p -> p.getData().length).sum();
        }

        public Stream<PathAndData> stream() {
            return set.stream().peek(p -> {
                current.getAndAdd(p.getData().length);
                setChanged();
                notifyObservers();
            });
        }

        public int getProgress() {
            return (int)((current.get() * 100.0f) / total);
        }
    }
}
