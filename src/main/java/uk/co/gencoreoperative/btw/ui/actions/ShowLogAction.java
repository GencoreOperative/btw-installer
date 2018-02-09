package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.panels.LogPanel;

/**
 * Triggers the display of the Log to the user.
 */
public class ShowLogAction extends AbstractAction {

    private Component parent;

    /**
     * Show the Log to the user.
     * @param parent The parent component that the modal dialog should
     *               appear in front of.
     */
    public ShowLogAction(Component parent) {
        this.parent = parent;
        putValue(Action.SMALL_ICON, Icons.SCRIPT.getIcon());
        putValue(Action.SHORT_DESCRIPTION, Strings.TOOLTIP_SHOW_LOG.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LogPanel panel = new LogPanel();
        LogPanel.show(panel, parent);
    }
}
