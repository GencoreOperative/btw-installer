package uk.co.gencoreoperative.btw.ui.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.actions.ChooseMinecraftHome;
import uk.co.gencoreoperative.btw.ui.actions.DefaultMinecraftHome;
import uk.co.gencoreoperative.btw.ui.actions.MinecraftHomeMenuAction;
import uk.co.gencoreoperative.btw.ui.actions.RemoveAction;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.version.Version;

public class MinecraftHomePanel extends JPanel {

    private final Context context;
    private final DialogFactory dialogFactory;
    private final JPopupMenu minecraftHomeMenu;

    public MinecraftHomePanel(Context context, DialogFactory dialogFactory) {
        this.context = context;
        this.dialogFactory = dialogFactory;
        minecraftHomeMenu = ChooseMinecraftHome.getMinecraftHomeMenu(context, dialogFactory);

        setBorder(new TitledBorder("Minecraft Home"));

        setLayout(new MigLayout("fillx, wrap 1, insets 10",
                "",
                "[min!]"));
        add(selectMinecraftHome(), "grow");
        add(versionPanel(context), "grow");
    }

    private JPanel selectMinecraftHome() {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 0",
                "[min!][][min!]",
                "[min!]"));

        // Row 1
        panel.add(new JLabel(Icons.FOLDER.getIcon()));

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

        // Advanced Menu Button
        final JButton minecraftMenuButton = new JButton();

        JPopupMenu menu = new JPopupMenu();
        menu.setInvoker(minecraftMenuButton);
        menu.add(new JMenuItem(new DefaultMinecraftHome(context)));
        menu.add(new JMenuItem(new ChooseMinecraftHome(context, dialogFactory)));
        menu.addSeparator();
        menu.add(new JMenuItem(new RemoveAction(this, context)));

        minecraftMenuButton.setAction(new MinecraftHomeMenuAction(menu));
        minecraftMenuButton.setToolTipText(Strings.TOOLTIP_ADVANCED.getText());
        panel.add(minecraftMenuButton, "wrap");
        return panel;
    }

    private JPanel versionPanel(Context context) {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 0",
                "[min!][grow]"));
        panel.add(new JLabel("Version:"));

        JLabel versionLabel = new JLabel();
        versionLabel.setFont(versionLabel.getFont().deriveFont(Font.ITALIC));
        versionLabel.setEnabled(false);
        context.register(InstalledVersion.class, (o, arg) -> {
            String text = "";
            if (context.contains(InstalledVersion.class)) {
                InstalledVersion installedVersion = context.get(InstalledVersion.class);
                Version version = installedVersion.getVersion().orElse(Version.NOT_RECOGNISED);
                text = version.getPatchVersion();
            }
            versionLabel.setText(text);
        });

        panel.add(versionLabel);

        return panel;
    }
}