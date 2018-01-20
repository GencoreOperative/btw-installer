package uk.co.gencoreoperative.btw.ui.panels;

import static uk.co.gencoreoperative.btw.ui.Strings.DIALOG_TITLE_PROGRESS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ui.Strings;

/**
 * Shows the patch installation progress dialog. This acts as a specific progress
 * panel for the user to indicate when the patch has completed.
 */
public class ProgressPanel extends JPanel {

    private final Map<State, JLabel> stateMap = new HashMap<>();

    /**
     * Build up the panel by enumerating the possible States of the patch progress
     * and representing these as labels for the user.
     */
    public ProgressPanel() {
        setLayout(new MigLayout("fillx, wrap 1",
                "[min!]"));

        Arrays.stream(State.values()).forEach(s -> {
            JLabel label = new JLabel();
            label.setText(s.getText());
            label.setIcon(s.getIcon());
            label.setEnabled(false);
            stateMap.put(s, label);
            add(label);
        });
    }

    /**
     * @param state The state (JLabel) on the panel to enable to indicate progress.
     */
    public void setState(State state) {
        stateMap.get(state).setEnabled(true);
    }

    /**
     * Helper method to create a suitable dialog which contains the progress panel.
     * @param parent Possibly null parent Dialog for model functionality
     * @param panel The initialised ProgressPanel to display
     * @return The dialog to display to the user, not visible, but otherwise initialised.
     */
    public JDialog createDialog(Dialog parent, ProgressPanel panel) {
        JDialog dialog = new JDialog(parent, true);
        dialog.setTitle(DIALOG_TITLE_PROGRESS.getText());

        dialog.setLayout(new BorderLayout());

        // Progress Panel - CENTER
        dialog.add(panel, BorderLayout.CENTER);

        // Buttons Panel - SOUTH
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));

        AbstractAction closeAction = new AbstractAction(Strings.BUTTON_CLOSE.getText()) {
            {
                putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false));
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        };
        buttons.add(new JButton(closeAction));

        dialog.add(buttons, BorderLayout.SOUTH);

        // Currently Close just closes dialog - no cancel.
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAction.actionPerformed(null);
            }
        });
        dialog.getRootPane().registerKeyboardAction(
                closeAction,
                (KeyStroke) closeAction.getValue(Action.ACCELERATOR_KEY),
                JComponent.WHEN_FOCUSED);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parent);

        return dialog;
    }

    /**
     * The possible states of installation for the patch.
     */
    public enum State {
        REMOVE_PREVIOUS("/icons/folder_delete.png", Strings.STATE_REMOVE_PREVIOUS.getText()),
        CREATE_FOLDER("/icons/folder_add.png", Strings.STATE_CREATE_FOLDER.getText()),
        COPY_JSON("/icons/page_go.png", Strings.STATE_COPY_JSON.getText()),
        CREATE_JAR("/icons/compress.png", Strings.STATE_CREATE_JAR.getText()),
        WRITE_VERSION("/icons/page_white_text.png", Strings.STATE_WRITE_VERSION.getText()),
        COMPLETE("/icons/accept.png", Strings.STATE_COMPLETE.getText());


        private String icon;
        private String text;

        State(String icon, String text) {
            this.icon = icon;
            this.text = text;
        }

        ImageIcon getIcon() {
            URL resource = ProgressPanel.class.getResource(icon);
            return new ImageIcon(resource);
        }

        public String getText() {
            return text;
        }
    }

    // Testing only
    public static void main(String... args) {
        ProgressPanel panel = new ProgressPanel();
        panel.createDialog(null, new ProgressPanel()).setVisible(true);
    }
}
