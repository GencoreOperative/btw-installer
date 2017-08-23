package uk.co.gencoreoperative.btw;

import java.io.File;
import java.util.function.Supplier;

/**
 * Responsible for providing the platform dependent location of Minecraft
 */
public class MineCraftPathResolver implements Supplier<File> {
    @Override
    public File get() {
        return new File("/Users/robert.wapshott/Library/Application Support/minecraft");
    }
}
