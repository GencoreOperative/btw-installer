package uk.co.gencoreoperative.btw.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

/**
 * The available icons that can be used in the user interfaces.
 */
public enum Icons {
    ARROW_RIGHT("arrow_right.png"),
    BIN_CLOSED("bin_closed.png"),
    FOLDER("folder.png"),
    COMPRESS("compress.png"),
    FOLDER_DELETE("folder_delete.png"),
    FOLDER_ADD("folder_add.png"),
    PAGE_GO("page_go.png"),
    PAGE_WHITE_TEXT("page_white_text.png"),
    ACCEPT("accept.png"),
    SQUID("squid.png"),
    MAGNIFIER("magnifier.png");

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
