package uk.co.gencoreoperative.btw.utils;

import static uk.co.gencoreoperative.btw.utils.FileUtils.copyStream;
import static uk.co.gencoreoperative.btw.utils.FileUtils.openZip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

    public static File writeStreamToFile(Stream<PathAndData> stream, File target) {
        try (ZipOutputStream targetStream = openZip(target)) {
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
}
