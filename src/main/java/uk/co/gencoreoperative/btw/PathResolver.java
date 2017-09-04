package uk.co.gencoreoperative.btw;

import java.io.File;
import java.util.function.Supplier;

/**
 * Responsible for providing the platform dependent location of Minecraft
 */
public class PathResolver implements Supplier<File> {
    private String path;

    public PathResolver(File path) {
        this(path.getAbsolutePath());
    }

    public PathResolver(String path) {
        this.path = path;
    }

    public PathResolver() {
        this("/Users/robert.wapshott/Library/Application Support/minecraft");
    }

    @Override
    public File get() {
        return new File(path);
    }

    public File versions() {
        return new File(get(), "versions");
    }

    public File oneFiveTwo() {
        return new File(versions(), "1.5.2");
    }

    public File betterThanWolves() {
        return new File(versions(), "BetterThanWolves");
    }

    public static File getDefaultMinecraftPath() {
        String operatingSystem = System.getProperty("os.name");
        File folder;
        if ("Mac OS X".equalsIgnoreCase(operatingSystem)) {
            folder = new File(System.getProperty("user.home") + "/Library/Application Support/minecraft");

        } else {
            folder = new File("%appdata%\\.minecraft");
        }
        return folder;
    }
}
