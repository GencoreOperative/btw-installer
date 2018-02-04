package uk.co.gencoreoperative.btw.version;

import java.util.HashMap;
import java.util.Map;

import uk.co.gencoreoperative.btw.ui.Strings;

/**
 * Models the version of the installed patch.
 *
 * Intended to be a generic storage structure which can be serialised
 * and will be generic enough to not change in future versions.
 *
 * Initially we are only interested in storing the version of the patch
 * that the user installed. However in the future we might be interested in
 * storing other information, e.g. Mods List.
 */
public class Version {

    /**
     * If there was a problem reading the current version information, then this
     * Version can be used to signal this to the user.
     */
    public static final Version NOT_RECOGNISED = new Version(Strings.NOT_RECOGNISED.getText());

    private static final String PATCH_VERSION = "patch.version";
    private final Map<String, String> properties = new HashMap<>();

    public Version(String patch) {
        properties.put(PATCH_VERSION, patch);
    }

    /**
     * @return Non null patch version of the installed patch.
     */
    public String getPatchVersion() {
        return properties.get(PATCH_VERSION);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        return properties != null ? properties.equals(version.properties) : version.properties == null;
    }

    @Override
    public int hashCode() {
        return properties != null ? properties.hashCode() : 0;
    }
}
