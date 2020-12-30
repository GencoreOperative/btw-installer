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
    TITLE("BTW Installer"),
    TITLE_VERSION("BTW Installer - " + VERSION.getText()),

    TITLE_REMOVE_CONFIRM("Confirm Removal"),
    TITLE_PROGRESS("Patching in progress"),

    BUTTON_CLOSE("Close"),
    BUTTON_DIALOG_SELECT("Select"),
    BUTTON_PATCH("Patch"),
    BUTTON_CHOOSE_PATCH("Choose..."),
    BUTTON_COPY_TO_CLIPBOARD("Copy to Clipboard"),
    BUTTON_CANCEL("Cancel"),
    BUTTON_ADD("Add..."),
    BUTTON_REMOVE("Remove..."),
    BUTTON_UP("Move Up"),
    BUTTON_DOWN("Move Down"),

    TOOLTIP_COPY_TO_CLIPBOARD("<html>Copy the contents of the <br>application log to the clipboard</html>"),
    TOOLTIP_SHOW_LOG("Show the current patch log"),
    TOOLTIP_PATCH("Start the patching process with the selected Patch Zip"),
    TOOLTIP_CLOSE("Close this window"),
    TOOLTIP_ADVANCED("Advanced configuration options"),
    TOOLTIP_SELECT_PATCH("Select a Better Than Wolves patch to apply"),
    TOOLTIP_SELECT_ADDON("Select an addon to apply"),
    TOOLTIP_SELECT_ZIP("Select the folder to import"),
    TOOLTIP_REMOVE_ADDON("Remove the addon selected"),
    TOOLTIP_ADDON_UP("Move the selected addon up"),
    TOOLTIP_ADDON_DOWN("Move the selected addon down"),
    TOOLTIP_CANCEL("Cancel importing addon"),

    MENU_DEFAULT_MINECRAFT_HOME("Default Minecraft Home"),
    MENU_CHANGE_MINECRAFT_HOME("Custom Minecraft Home..."),

    REMOVE_QUESTION("Are you sure you want to remove Better Than Wolves?"),

    SELECT_ZIP_TITLE("Select BetterThanWolves Zip"),
    SELECT_MC_HOME("Select Minecraft Installation folder"),
    SELECT_ADDON_TITLE("Select a BetterThanWolves Addon"),

    MSG_PATCH_SUCCESS("Better Than Wolves was successfully installed"),
    MSG_REMOVE_SUCCESS("Better Than Wolves was removed"),
    ERROR_TITLE("Error"),
    ERROR_DETAIL("An error occurred whilst processing\n{0}\nReason:\n{1}"),
    CANCELLED_DETAIL("Cancelled by user"),
    INFORMATION_TITLE("Information"),

    STATE_COPY_JSON("Write JSON"),
    STATE_CREATE_JAR("Writing Jar"),
    STATE_ADD_ADDONS("Adding addons"),
    STATE_WRITE_VERSION("Writing version"),
    STATE_COMPLETE("Patch complete"),
    STATE_LOCATE_1_5_2("Copying 1.5.2 Client Jar"),
    STATE_CLEAN_PREVIOUS("Clean Previous Installation"),

    ACTION_REMOVE_PATCH("Remove Patch..."),

    NOT_RECOGNISED("Not Recognised");


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
