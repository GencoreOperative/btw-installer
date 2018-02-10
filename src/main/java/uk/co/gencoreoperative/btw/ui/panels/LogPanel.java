package uk.co.gencoreoperative.btw.ui.panels;

import static uk.co.gencoreoperative.btw.ui.ToolTipHelper.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.stream.Collectors;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ui.Icons;
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
    private CopyToClipboardAction action = new CopyToClipboardAction(() -> logArea.getText());

    public LogPanel() {
        setTitle("Patch Log");
        setIconImage(Icons.SQUID.getIcon().getImage());
        setModal(true);
        setResizable(false);

        setLayout(new BorderLayout());
        
        add(initialiseComponents(), BorderLayout.CENTER);
        add(initialiseButtons(), BorderLayout.SOUTH);

        CloseAction.apply(this, closeAction);
    }

    private JPanel initialiseComponents() {
        JPanel panel = new JPanel(new MigLayout(
                "insets 10",
                "",
                ""));

        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        logArea.setText(Logger.getLines().stream().collect(Collectors.joining("\n")));
        panel.add(new JScrollPane(logArea), "grow");
        return panel;
    }

    private JPanel initialiseButtons() {
        FlowLayout flowLayout = new FlowLayout(FlowLayout.TRAILING);
        flowLayout.setAlignOnBaseline(true);
        JPanel panel = new JPanel(flowLayout);

        panel.add(withToolTip(new JButton(action)));
        panel.add(new JButton(closeAction));
        return panel;
    }

    /**
     * Given a {@link JDialog} show the dialog by preparing it for showing to the user.
     *
     * @param dialog Non null dialog to show.
     * @param parent Possibly null component the dialog should appear in-front of.
     * @throws NullPointerException If dialog was null.
     */
    public static void show(JDialog dialog, Component parent) throws NullPointerException {
        if (dialog == null) throw new NullPointerException();
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Hide the Dialog and dispose of it.
     * @param dialog Non null.
     */
    public static void hide(JDialog dialog) {
        dialog.setVisible(false);
        dialog.dispose();
    }

    public static void main(String... args) {
        Logger.info("Testing testing testing...");
        LogPanel logPanel = new LogPanel();
        show(logPanel, null);
    }
}
