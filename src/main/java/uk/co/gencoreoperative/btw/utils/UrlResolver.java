package uk.co.gencoreoperative.btw.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Responsible for providing InputStreams for a given requested URL.
 */
public class UrlResolver {
    public InputStream streamURLContents(URL url) throws IOException {
        return url.openStream();
    }
}
