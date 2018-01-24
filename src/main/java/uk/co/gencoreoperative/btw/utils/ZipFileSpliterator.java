package uk.co.gencoreoperative.btw.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static uk.co.gencoreoperative.btw.utils.FileUtils.copyStream;

/**
 * Wraps around the Java Zip classes to provide a handy stream of the zip contents.
 */
public class ZipFileSpliterator implements Spliterator<EntryAndData> {

    private final ZipInputStream inputStream;

    public ZipFileSpliterator(final InputStream stream) {
        inputStream = new ZipInputStream(stream);
    }

    @Override
    public boolean tryAdvance(Consumer<? super EntryAndData> action) {
        try {
            ZipEntry entry = inputStream.getNextEntry();
            if (entry == null) {
                inputStream.close();
                return false;
            }

            final EntryAndData result;
            if (entry.isDirectory()) {
                result = EntryAndData.folder(entry);
            } else {
                ByteArrayOutputStream zipContents = new ByteArrayOutputStream();
                copyStream(inputStream, zipContents, false, true);
                result = EntryAndData.file(entry, new ByteArrayInputStream(zipContents.toByteArray()));
            }

            action.accept(result);

            return true;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Spliterator<EntryAndData> trySplit() {
        return null; // ZipFile cannot be accessed in parallel
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE; // Size cannot be known without reading the entire stream first.
    }

    @Override
    public int characteristics() {
        return DISTINCT | NONNULL;
    }

    /**
     * Given an {@link InputStream} of a zip archive, stream the entries of the zip.
     * @param stream Non null stream which must contain a zip archive.
     * @return Non null stream of its contents.
     */
    public static Stream<EntryAndData> streamZip(InputStream stream) {
        return StreamSupport.stream(new ZipFileSpliterator(stream), false);
    }
}