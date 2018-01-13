package uk.co.gencoreoperative.btw;

import java.io.File;
import java.util.function.Supplier;

import uk.co.gencoreoperative.btw.utils.OSUtils;

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
        this(getDefaultMinecraftPath());
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
        if (OSUtils.isWindows()) {
            String appdata = System.getenv("APPDATA");
            return folder(appdata, ".minecraft");
        } else if (OSUtils.isMacOS()) {
            String home = System.getProperty("user.home");
            return folder(folder(folder(home, "Library"), "Application Support"), "minecraft");
        } else {
            return new File(System.getProperty("user.dir"));
        }
    }

    private static File folder(String parent, String child) {
        return new File(parent, child);
    }

    private static File folder(File parent, String child) {
        return new File(parent, child);
    }
}
