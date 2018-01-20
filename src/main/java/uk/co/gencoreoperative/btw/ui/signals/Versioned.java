package uk.co.gencoreoperative.btw.ui.signals;

/**
 * Represents a signal that contains version information. Useful for
 * consumers of the signal to extract the version information
 * regardless of the signal type.
 */
public interface Versioned {
    String getVersion();
    default boolean isVersioned() {
        return getVersion() != null;
    }
    void setVersion(String version);
}
