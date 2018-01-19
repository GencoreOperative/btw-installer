package uk.co.gencoreoperative.btw.ui.panels;

import javax.swing.*;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ui.Progress;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.workers.PatchWorker;

/**
 * Shows the patch installation progress via a specific progress panel.
 */
public class ProgressPanel extends JPanel {

    private final Map<State, JLabel> stateMap = new HashMap<>();

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

        //TODO Working on Close button
    }

    public static void main(String... args) {
        JDialog dialog = new JDialog();
        dialog.add(new ProgressPanel());
        dialog.pack();
        dialog.setVisible(true);
    }

    public void setState(State state) {
        stateMap.get(state).setEnabled(true);
    }

    public enum State {
        REMOVE_PREVIOUS("/icons/folder_delete.png", Strings.STATE_REMOVE_PREVIOUS.getText()),
        CREATE_FOLDER("/icons/folder_add.png", Strings.STATE_CREATE_FOLDER.getText()),
        COPY_JSON("/icons/page_go.png", Strings.STATE_COPY_JSON.getText()),
        CREATE_JAR("/icons/compress.png", Strings.STATE_CREATE_JAR.getText()),
        WRITE_VERSION("/icons/page_white_text.png", Strings.STATE_WRITE_VERSION.getText());


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
}
