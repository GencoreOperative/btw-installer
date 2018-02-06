package uk.co.gencoreoperative.btw.ui.panels;

import static uk.co.gencoreoperative.btw.ui.Strings.TITLE_PROGRESS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.Strings;

/**
 * Shows the patch installation progress dialog. This acts as a specific progress
 * panel for the user to indicate when the patch has completed.
 */
public class ProgressPanel extends JPanel {

    private final JProgressBar progressBar;
    private final Map<State, JLabel> labels = new HashMap<>();

    /**
     * Build up the panel by enumerating the possible States of the patch progress
     * and representing these as labels for the user.
     */
    public ProgressPanel() {
        setLayout(new MigLayout("wrap 1",
                "[min!]"));


        // State Rows
        Arrays.stream(State.values()).forEach(s -> {
            JLabel label = new JLabel(s.getText(), s.getIcon(), JLabel.LEADING);
            label.setEnabled(false);
            labels.put(s, label);
            add(label);
        });

        // Progress Row
        progressBar = new JProgressBar(0, 100);
        add(progressBar, "grow");
    }

    /**
     * @param state The state (JLabel) on the panel to enable to indicate progress.
     */
    public void setState(State state) {
        labels.get(state).setEnabled(true);
    }

    public void setProgress(int current) {
        progressBar.setValue(current);
    }

    /**
     * Helper method to create a suitable dialog which contains the progress panel.
     * @param parent Possibly null parent Dialog for model functionality
     * @param panel The initialised ProgressPanel to display
     * @return The dialog to display to the user, not visible, but otherwise initialised.
     */
    public JDialog createDialog(Frame parent, ProgressPanel panel) {
        JDialog dialog = new JDialog(parent, true);
        dialog.setTitle(TITLE_PROGRESS.getText());

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
        CLEAN_PREVIOUS_INSTALLATION(Icons.BIN_EMPTY.getIcon(), Strings.STATE_CLEAN_PREVIOUS.getText()),
        COPY_1_5_2(Icons.MAGNIFIER.getIcon(), Strings.STATE_LOCATE_1_5_2.getText()),
        COPY_JSON(Icons.PAGE_GO.getIcon(), Strings.STATE_COPY_JSON.getText()),
        CREATE_JAR(Icons.COMPRESS.getIcon(), Strings.STATE_CREATE_JAR.getText()),
        WRITE_VERSION(Icons.PAGE_WHITE_TEXT.getIcon(), Strings.STATE_WRITE_VERSION.getText()),
        COMPLETE(Icons.ACCEPT.getIcon(), Strings.STATE_COMPLETE.getText());


        private ImageIcon icon;
        private String text;

        State(ImageIcon icon, String text) {
            this.icon = icon;
            this.text = text;
        }

        ImageIcon getIcon() {
            return icon;
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
