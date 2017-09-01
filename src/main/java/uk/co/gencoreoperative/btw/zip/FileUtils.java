package uk.co.gencoreoperative.btw.zip;

import java.io.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    private FileUtils() {
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
            try {
                if (closeInput) input.close();
                if (closeOutput) output.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static InputStream read(final File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static OutputStream write(final File file) {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static ZipOutputStream openZip(final File file) {
        try {
            return new ZipOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Stream<ZipFileSpliterator.ZipFileEntryAndData> streamZip(File file) {
        try {
            return streamZip(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Stream<ZipFileSpliterator.ZipFileEntryAndData> streamZip(InputStream stream) {
        return StreamSupport.stream(new ZipFileSpliterator(stream), false);
    }
}
