package uk.co.gencoreoperative.btw.actions;

import java.io.File;

import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.FileChooserPreferences;
import uk.co.gencoreoperative.btw.ui.Strings;

/**
 * Functions which request something specific from the user.
 */
public class Request {
    private static final String MINECRAFT_LOCATION = "minecraft.location";
    private static final String PATCH_LOCATION = "patch.location";
    private static final String ADDON_LOCATION = "addon.location";

    private Request() {
    }

    public static File requestMinecraftHome(DialogFactory dialogFactory) {
        File previous = FileChooserPreferences.getLastOpenedPath(MINECRAFT_LOCATION);
        File selected = dialogFactory.requestFolderLocation(
                Strings.SELECT_MC_HOME,
                previous,
                PathResolver.getDefaultMinecraftPath(),
                File::isDirectory);
        if (selected != null) {
            FileChooserPreferences.setLastOpenedPath(MINECRAFT_LOCATION, selected);
        }
        return selected;
    }

    public static File requestPatchZip(DialogFactory dialogFactory) {
        File previous = FileChooserPreferences.getLastOpenedPath(PATCH_LOCATION);
        File selected = dialogFactory.requestFileLocation(
                Strings.SELECT_ZIP_TITLE,
                previous,
                new File(System.getProperty("user.home")),
                file -> file.getName().toLowerCase().endsWith("zip"));
        if (selected != null) {
            FileChooserPreferences.setLastOpenedPath(PATCH_LOCATION, selected);
        }
        return selected;
    }

    public static File requestAddonZip(DialogFactory dialogFactory) {
        File previous = FileChooserPreferences.getLastOpenedPath(ADDON_LOCATION);
        File selected = dialogFactory.requestFileLocation(
                Strings.SELECT_ADDON_TITLE,
                previous,
                new File(System.getProperty("user.home")),
                file -> file.getName().toLowerCase().endsWith("zip"));
        if (selected != null) {
            FileChooserPreferences.setLastOpenedPath(ADDON_LOCATION, selected);
        }
        return selected;
    }
}
