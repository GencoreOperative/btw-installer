package uk.co.gencoreoperative.btw.ui;

import static java.text.MessageFormat.format;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import uk.co.gencoreoperative.btw.ui.panels.MinecraftHomePanel;

/**
 * Provides a simple label with system information in the tool tip.
 */
public class AboutLabel extends JLabel {
    private final Map<String, String> properties = new HashMap<>();

    public AboutLabel() {
        super(new ImageIcon(MinecraftHomePanel.class.getResource("/icons/computer.png")));

        properties.put("installer", Strings.VERSION.getText());
        property(properties, "os.name");
        property(properties, "os.arch");
        property(properties, "os.version");
        property(properties, "java.version");
        property(properties, "java.vendor");

        String text = properties.keySet().stream()
                .map(k -> format("<b>{0}</b>: {1}", k, properties.get(k)))
                .collect(Collectors.joining("<br>"));
        setToolTipText(format("<html>{0}</html>", text));
    }

    private static void property(Map<String, String> map, String key) {
        map.put(key, System.getProperty(key));
    }
}
