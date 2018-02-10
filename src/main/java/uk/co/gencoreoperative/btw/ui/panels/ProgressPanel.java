package uk.co.gencoreoperative.btw.ui.panels;

import static uk.co.gencoreoperative.btw.ui.Strings.TITLE_PROGRESS;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.actions.CloseAction;

/**
 * Shows the patch installation progress dialog. This acts as a specific progress
 * panel for the user to indicate when the patch has completed.
 */
public class ProgressPanel extends JDialog {

    private JProgressBar progressBar;
    private final Map<State, JLabel> labels = new HashMap<>();

    /**
     * Build up the panel by enumerating the possible States of the patch progress
     * and representing these as labels for the user.
     */
    public ProgressPanel() {
        setModal(true);
        setTitle(TITLE_PROGRESS.getText());

        setLayout(new BorderLayout());

        // Progress Panel - CENTER
        add(initialiseRows(), BorderLayout.CENTER);

        // Buttons Panel - SOUTH
        CloseAction closeAction = new CloseAction(this, false);
        add(initialiseButton(closeAction), BorderLayout.SOUTH);
        CloseAction.apply(this, closeAction);
    }

    private JPanel initialiseRows() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 10",
                "[min!]"));


        // State Rows
        Arrays.stream(State.values()).forEach(s -> {
            JLabel label = new JLabel(s.getText(), s.getIcon(), JLabel.LEADING);
            label.setEnabled(false);
            labels.put(s, label);
            panel.add(label);
        });

        // Progress Row
        progressBar = new JProgressBar(0, 100);
        panel.add(progressBar, "grow");
        return panel;
    }

    private JPanel initialiseButton(CloseAction closeAction) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(new JButton(closeAction));
        return panel;
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
        LogPanel.show(null, panel);
    }
}
