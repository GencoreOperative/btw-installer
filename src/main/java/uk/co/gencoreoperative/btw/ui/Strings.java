package uk.co.gencoreoperative.btw.ui;

import static java.text.MessageFormat.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

/**
 * Defines the strings that are used for display purposes in the application.
 */
public enum Strings {
    APP_NAME("Patch Installer"),
    VERSION(readProperty("/app.version")),
    TITLE(format("{0} - {1}", APP_NAME.getText(), VERSION.getText())),
    BUTTON_CLOSE("Close"),
    BUTTON_SELECT("Select"),
    BUTTON_PATCH("Patch"),
    SELECT_ZIP_TITLE("Select BetterThanWolves Zip"),
    SELECT_MC_HOME("Select Minecraft Installation folder"),
    SUCCESS_TITLE("Patch complete"),
    SUCCESS_MSG("Patch was successfully applied"),
    ERROR_TITLE("Error"),
    ERROR_DETAIL("An error occurred whilst processing:\n{0}\nReason:\n{1}"),
    CANCELLED_DETAIL("Cancelled by user"),
    INFORMATION_TITLE("Information"),
    CONFIRM_DEFAULT_MESSAGE("Minecraft installation detected, use the default Minecraft installation?"),
    CONFIRM_DEFAULT_TITLE("Confirm default Minecraft installation?");

    private final String text;

    Strings(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    private static String readProperty(String path) {
        InputStream stream = Strings.class.getResourceAsStream(path);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.readLine();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
