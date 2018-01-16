package uk.co.gencoreoperative.btw.utils;

import static java.text.MessageFormat.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.zip.ZipEntry;

/**
 * A simple model object to represent a file path and the data contained within
 * that path. Initially intended for use when working with zip files but could
 * apply more generally.
 *
 * File paths are represented as {@code String[]} for cross platform simplicity.
 *
 * Byte data is stored in these objects which will consume memory in the system.
 */
public class PathAndData {
    private String path;
    private final byte[] data;

    public PathAndData(String path, byte[] data) {
        this.path = path;
        this.data = data;
    }

    /**
     * @return The data for this file in a streamable format.
     */
    public byte[] getData() {
        return data;
    }

    public InputStream getDataStream() {
        return new ByteArrayInputStream(data);
    }

    /**
     * @return The path for this file in String[] format.
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDirectory() {
        return data == null;
    }

    public static PathAndData fromZipEntry(ZipEntry entry, InputStream data) {
        String name = entry.getName();
        if (name.startsWith("/")) name = name.substring("/".length(), name.length());
        if (name.endsWith("/")) name = name.substring(0, name.length()-"/".length());

        if (entry.isDirectory()) {
            return new PathAndData(name, null);
        }

        // Else this is file and has data.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileUtils.copyStream(data, output, false, true);
        return new PathAndData(name, output.toByteArray());
    }

    public static PathAndData fromZipEntry(ZipEntry entry) {
        return fromZipEntry(entry, null);
    }

    public boolean isFile() {
        return !isDirectory();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathAndData that = (PathAndData) o;

        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return format("{0} ({1} bytes)", path, data.length);
    }
}
