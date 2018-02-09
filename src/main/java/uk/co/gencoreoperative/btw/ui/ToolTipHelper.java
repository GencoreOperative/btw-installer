package uk.co.gencoreoperative.btw.ui;

import javax.swing.*;

/**
 * Provides utility methods to help apply a tool tip to a component.
 */
public class ToolTipHelper {
    public static JButton withToolTip(JButton button) {
        Action action = button.getAction();
        Object value = action.getValue(Action.SHORT_DESCRIPTION);
        if (value != null) {
            button.setToolTipText(String.valueOf(value));
        }
        return button;
    }
}
