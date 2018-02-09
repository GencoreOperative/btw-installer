package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.function.Supplier;

import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.Strings;

public class CopyToClipboardAction extends AbstractAction {

    private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    private final Supplier<String> contents;

    public CopyToClipboardAction(Supplier<String> contents) {
        this.contents = contents;
        putValue(Action.NAME, Strings.BUTTON_COPY_TO_CLIPBOARD.getText());
        putValue(Action.SMALL_ICON, Icons.SCRIPT.getIcon());
        putValue(Action.SHORT_DESCRIPTION, Strings.TOOLTIP_COPY_TO_CLIPBOARD.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setClipboard(contents.get());
    }
    private static void setClipboard(String text) {
        clipboard.setContents(new StringSelection(text), null);
    }
}
