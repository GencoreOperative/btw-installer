package uk.co.gencoreoperative.btw.utils;

import java.io.ByteArrayInputStream;
import java.util.zip.ZipEntry;

/**
 * Models a ZipEntry and its data (byte[]) in a single value object.
 */
public class EntryAndData {

    private ZipEntry entry;
    private ByteArrayInputStream data;

    /**
     * @param entry non null ZipEntry.
     * @param data Optionally null data for directories.
     */
    private EntryAndData(ZipEntry entry, ByteArrayInputStream data) {
        this.entry = entry;
        this.data = data;
    }

    public String getName() {
        return entry.getName();
    }

    public ZipEntry getEntry() {
        return entry;
    }

    public ByteArrayInputStream getData() {
        return data;
    }

    public boolean isFile() {
        return data != null;
    }

    public static EntryAndData file(ZipEntry entry, ByteArrayInputStream data) {
        return new EntryAndData(entry, data);
    }

    public static EntryAndData folder(ZipEntry entry) {
        return new EntryAndData(entry, null);
    }
}
