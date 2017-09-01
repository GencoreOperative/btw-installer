package uk.co.gencoreoperative.btw.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static uk.co.gencoreoperative.btw.utils.FileUtils.copyStream;

public class ZipFileSpliterator implements Spliterator<ZipFileSpliterator.ZipFileEntryAndData> {

    private final ZipInputStream inputStream;

    public ZipFileSpliterator(final InputStream stream) {
        inputStream = new ZipInputStream(stream);
    }

    @Override
    public boolean tryAdvance(Consumer<? super ZipFileEntryAndData> action) {
        try {
            ZipEntry entry = inputStream.getNextEntry();
            if (entry == null) {
                inputStream.close();
                return false;
            }

            ZipFileEntryAndData result = new ZipFileEntryAndData();
            result.entry = entry;
            if (!entry.isDirectory()) {
                ByteArrayOutputStream zipContents = new ByteArrayOutputStream();
                copyStream(inputStream, zipContents, false, true);
                result.data = new ByteArrayInputStream(zipContents.toByteArray());
            }

            action.accept(result);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Spliterator<ZipFileEntryAndData> trySplit() {
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

    public static class ZipFileEntryAndData {
        public ZipEntry entry;
        public ByteArrayInputStream data;
    }
}
