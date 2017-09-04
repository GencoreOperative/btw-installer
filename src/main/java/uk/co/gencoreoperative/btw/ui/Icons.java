package uk.co.gencoreoperative.btw.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

/**
 * The available icons that can be used in the user interfaces.
 */
public enum Icons {
    QUESTION("black-question-mark-ornament_2753.png"),
    TICK("white-heavy-check-mark_2705.png"),
    ERROR("cross-mark_274c.png");

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
