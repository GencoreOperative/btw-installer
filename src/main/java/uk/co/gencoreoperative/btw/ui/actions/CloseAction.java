package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import uk.co.gencoreoperative.btw.ui.Strings;

public class CloseAction extends AbstractAction {
    private final Component dialog;

    public CloseAction(Component dialog) {
        this.dialog = dialog;
        putValue(Action.NAME, Strings.BUTTON_CLOSE.getText());
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.setVisible(false);
        System.exit(0);
    }
}
