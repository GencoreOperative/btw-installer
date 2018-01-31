package uk.co.gencoreoperative.btw.ui.signals;

import java.io.File;

/**
 * Represents the client Jar needed for installation of the BTW client patch.
 */
public class ClientJar {
    private final File client;

    public ClientJar(File client) {
        this.client = client;
    }

    public File getClient() {
        return client;
    }
}
