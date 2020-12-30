package uk.co.gencoreoperative.btw.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

/**
 * The available icons that can be used in the user interfaces.
 */
public enum Icons {
    ACCEPT("accept.png"),
    ADD("add.png"),
    DELETE("delete.png"),
    ARROW_UP("arrow_up.png"),
    ARROW_DOWN("arrow_down.png"),
    ARROW_RIGHT("arrow_right.png"),
    BIN_CLOSED("bin_closed.png"),
    COMPRESS("compress.png"),
    FOLDER("folder.png"),
    FOLDER_ADD("folder_add.png"),
    FOLDER_DELETE("folder_delete.png"),
    FOLDER_EDIT("folder_edit.png"),
    MAGNIFIER("magnifier.png"),
    PAGE_GO("page_go.png"),
    PAGE_WHITE_TEXT("page_white_text.png"),
    SQUID("squid.png"), COG("cog.png"),
    BIN_EMPTY("bin_empty.png"),
    SCRIPT("script.png");

    private final ImageIcon icon;
    Icons(String path) {
        try {
            icon = new ImageIcon(ImageIO.read(Icons.class.getResource("/icons/" + path)));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @return non null Icon.
     */
    public ImageIcon getIcon() {
        return icon;
    }
}
