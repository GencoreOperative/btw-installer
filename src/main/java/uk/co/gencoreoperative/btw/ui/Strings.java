package uk.co.gencoreoperative.btw.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Defines the strings that are used for display purposes in the application.
 */
public enum Strings {
    VERSION(readProperty("/app.version")),
    TITLE_PATCH("BTW Installer - " + VERSION.getText()),
    TITLE_REMOVE("BTW Remover - " + VERSION.getText()),

    TITLE_PATCH_SUCCESS("Patch complete"),
    TITLE_REMOVE_SUCCESS("Removal complete"),

    BUTTON_CLOSE("Close"),
    BUTTON_CHANGE_MINECRAFT_HOME("Change..."),
    BUTTON_DIALOG_SELECT("Select"),
    BUTTON_PATCH("Patch"),
    BUTTON_REMOVE("Remove"),
    BUTTON_CHOOSE_PATCH("Choose..."),

    SELECT_ZIP_TITLE("Select BetterThanWolves Zip"),
    SELECT_MC_HOME("Select Minecraft Installation folder"),

    MSG_PATCH_SUCCESS("Better Than Wolves was successfully installed"),
    MSG_REMOVE_SUCCESS("Better Than Wolves was removed"),
    ERROR_TITLE("Error"),
    ERROR_DETAIL("An error occurred whilst processing\n{0}\nReason:\n{1}"),
    CANCELLED_DETAIL("Cancelled by user"),
    INFORMATION_TITLE("Information"),
    CONFIRM_DEFAULT_MESSAGE("Default Minecraft installation detected, use this installation?"),
    CONFIRM_DEFAULT_TITLE("Confirm default Minecraft installation"),

    STATE_REMOVE_PREVIOUS("Remove previous"),
    STATE_CREATE_FOLDER("Create installation folder"),
    STATE_COPY_JSON("Write JSON"),
    STATE_CREATE_JAR("Writing Jar"),
    STATE_WRITE_VERSION("Writing version"),
    STATE_COMPLETE("Patch complete"),

    DIALOG_TITLE_PROGRESS("Patching in progress");


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
