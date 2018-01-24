package uk.co.gencoreoperative.btw.utils;

import static uk.co.gencoreoperative.btw.utils.FileUtils.copyStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileStream implements Spliterator<PathAndData> {
    private final ZipInputStream inputStream;

    public ZipFileStream(final InputStream stream) {
        inputStream = new ZipInputStream(stream);
    }

    @Override
    public boolean tryAdvance(Consumer<? super PathAndData> action) {
        try {
            ZipEntry entry = inputStream.getNextEntry();
            if (entry == null) {
                inputStream.close();
                return false;
            }

            final PathAndData result;
            if (entry.isDirectory()) {
                result = PathAndData.fromZipEntry(entry);
            } else {
                ByteArrayOutputStream zipContents = new ByteArrayOutputStream();
                copyStream(inputStream, zipContents, false, true);
                result = PathAndData.fromZipEntry(entry, new ByteArrayInputStream(zipContents.toByteArray()));
            }

            action.accept(result);

            return true;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Spliterator<PathAndData> trySplit() {
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
    public static Stream<PathAndData> streamZip(InputStream stream) {
        return StreamSupport.stream(new ZipFileStream(stream), false);
    }

    /**
     * Write the given file representations to the target zip file.
     * <p>
     * This operation will iterate over each entry in the stream and write it to the
     * zip archive.
     *
     * @param stream Non null, possibly empty stream.
     * @param target Non null zip file, which will be overwritten.
     * @return The created zip file.
     */
    public static File writeStreamToZipFile(Stream<PathAndData> stream, File target) {
        try (ZipOutputStream targetStream = new ZipOutputStream(new FileOutputStream(target))) {
            Consumer<PathAndData> copyToTargetJar = p -> {
                try {
                    targetStream.putNextEntry(new ZipEntry(p.getPath()));
                    copyStream(p.getDataStream(), targetStream, true, false);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            };
            stream.forEach(copyToTargetJar);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return target;
    }

    /**
     * Open the zip file and stream its contents.
     *
     * @param file Non null file which must contain zip data.
     * @return Non null, possibly empty stream of the contents.
     */
    public static Stream<PathAndData> streamZip(File file) {
        try {
            return streamZip(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
