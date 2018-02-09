package uk.co.gencoreoperative.btw.ui.panels;

import static uk.co.gencoreoperative.btw.ui.ToolTipHelper.*;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ui.actions.CloseAction;
import uk.co.gencoreoperative.btw.ui.actions.CopyToClipboardAction;
import uk.co.gencoreoperative.btw.utils.Logger;

/**
 * A simple text dialog which shows the user the contents of the application
 * log.
 *
 * Provides the ability for the user to copy to the clipboard the log contents.
 */
public class LogPanel extends JDialog {

    private CloseAction closeAction = new CloseAction(LogPanel.this, false);
    private JTextArea logArea;

    public LogPanel() {
        setLayout(new BorderLayout());
        setTitle("Patch Log");
        setModal(true);

        add(initialiseComponents(), BorderLayout.CENTER);
        add(initialiseButtons(), BorderLayout.SOUTH);

        CloseAction.apply(this, closeAction);
    }

    private JPanel initialiseComponents() {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 10",
                "[pref!]",
                ""));

        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        logArea.setText(Logger.getLines().stream().collect(Collectors.joining("\n")));
        panel.add(logArea, "grow");
        return panel;
    }

    private JPanel initialiseButtons() {
        FlowLayout flowLayout = new FlowLayout(FlowLayout.TRAILING);
        flowLayout.setAlignOnBaseline(true);
        JPanel panel = new JPanel(flowLayout);
        panel.add(withToolTip(new JButton(new CopyToClipboardAction(() -> logArea.getText()))));
        panel.add(new JButton(closeAction));
        return panel;
    }

    public static void show(JDialog dialog, Component parent) {
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void main(String... args) {
        Logger.info("Testing testing testing...");
        LogPanel logPanel = new LogPanel();
        show(logPanel, null);
    }
}
