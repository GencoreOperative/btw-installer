package uk.co.gencoreoperative.btw.ui.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.actions.ChooseMinecraftHome;
import uk.co.gencoreoperative.btw.ui.actions.ChoosePatch;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.ui.signals.Versioned;

public class MinecraftHomePanel extends JPanel {

    private static final ImageIcon FOLDER = new ImageIcon(MinecraftHomePanel.class.getResource("/icons/folder.png"));
    private final Context context;
    private final ActionFactory actionFactory;

    public MinecraftHomePanel(Context context, ActionFactory actionFactory) {
        this.context = context;
        this.actionFactory = actionFactory;

        setBorder(new TitledBorder("Minecraft Home"));

        setLayout(new MigLayout("fillx, wrap 1, insets 10",
                "",
                "[min!]"));
        add(selectMinecraftHome());
        add(versionPanel(context, InstalledVersion.class));
    }

    private JPanel selectMinecraftHome() {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 0",
                "[min!][][min!]",
                "[min!]"));

        // Row 1
        final JLabel folderIcon = new JLabel(FOLDER);
        panel.add(folderIcon);
        final JTextField homeTextField = new JTextField(20);
        homeTextField.setEnabled(true);
        homeTextField.setEditable(false);
        context.register(MinecraftHome.class, (o, arg) -> {
            String text = "";
            if (context.contains(MinecraftHome.class)) {
                text = context.get(MinecraftHome.class).getFolder().getAbsolutePath();
            }
            homeTextField.setText(text);
        });
        panel.add(homeTextField, "grow");
        panel.add(new JButton(new ChooseMinecraftHome(context, actionFactory)), "wrap");
        return panel;
    }

    static JPanel versionPanel(Context context, Class<? extends Versioned> type) {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 0",
                "[min!][grow]"));
        panel.add(new JLabel("Version:"));
        JLabel versionLabel = new JLabel();
        versionLabel.setFont(versionLabel.getFont().deriveFont(Font.ITALIC));
        versionLabel.setEnabled(false);
        register(context, type, versionLabel);
        panel.add(versionLabel);
        return panel;
    }

    static void register(Context context, Class<? extends Versioned> type, JLabel field) {
        context.register(type, (o, arg) -> {
            String text = "";
            if (context.contains(type)) {
                text = context.get(type).getVersion();
            }
            field.setText(text);
        });
    }
}