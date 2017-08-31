package uk.co.gencoreoperative.btw;

import java.io.*;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileSpliterator implements Spliterator<ZipFileSpliterator.ZipFileEntryAndData> {

    private final ZipInputStream inputStream;

    public ZipFileSpliterator(final ZipInputStream stream) {
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
                Main.copyStream(inputStream, zipContents, false, true);
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
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return DISTINCT | NONNULL;
    }

    public static Stream<ZipFileEntryAndData> streamZip(File file) {
        try {
            return StreamSupport.stream(new ZipFileSpliterator(new ZipInputStream(new FileInputStream(file))), false);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Stream<ZipFileEntryAndData> streamZip(InputStream stream) {
        return StreamSupport.stream(new ZipFileSpliterator(new ZipInputStream(stream)), false);
    }

    public class ZipFileEntryAndData {
        ZipEntry entry;
        ByteArrayInputStream data;
    }
}
