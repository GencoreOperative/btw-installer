package uk.co.gencoreoperative.btw;

import java.io.File;
import java.util.function.Supplier;

import static java.text.MessageFormat.format;

public class FileCheck implements Check {
    private Supplier<File> resolver;

    public FileCheck(final File path) {
        this(() -> path);
    }

    public FileCheck(final Supplier<File> resolver) {
        this.resolver = resolver;
    }

    @Override
    public boolean check() {
        return resolver.get().exists();
    }

    @Override
    public String item() {
        return format("Folder: {0}", resolver.get().getAbsolutePath());
    }
}
