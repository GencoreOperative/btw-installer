package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import uk.co.gencoreoperative.btw.ui.Strings;

public class CloseAction extends AbstractAction {
    private final Component dialog;
    private boolean exit;

    public CloseAction(Component dialog, boolean exit) {
        this.dialog = dialog;
        this.exit = exit;
        putValue(Action.NAME, Strings.BUTTON_CLOSE.getText());
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false));
        putValue(Action.SHORT_DESCRIPTION, Strings.TOOLTIP_CLOSE.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.setVisible(false);
        if (exit) {
            System.exit(0);
        }
    }

    // TODO - Can we de-duplicate these?
    public static void apply(JDialog dialog, Action close) {
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close.actionPerformed(null);
            }
        });
        dialog.getRootPane().registerKeyboardAction(
                close,
                (KeyStroke) close.getValue(Action.ACCELERATOR_KEY),
                JComponent.WHEN_FOCUSED);
    }

    public static void apply(JFrame frame, Action close) {
        frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close.actionPerformed(null);
            }
        });
        frame.getRootPane().registerKeyboardAction(
                close,
                (KeyStroke) close.getValue(Action.ACCELERATOR_KEY),
                JComponent.WHEN_FOCUSED);
    }
}
