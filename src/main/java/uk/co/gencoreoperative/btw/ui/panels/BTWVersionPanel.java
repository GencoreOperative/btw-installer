package uk.co.gencoreoperative.btw.ui.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;

public class BTWVersionPanel extends JPanel {
    public BTWVersionPanel(ActionFactory factory, Context context) {
        setBorder(new TitledBorder("Versions"));
        setLayout(new MigLayout(
                "fillx, wrap 2",
                "[min!][]"));

        // Row 1
        add(new JLabel("Installed Version"));
        JTextField installedVersionField = new JTextField();
        installedVersionField.setEditable(false);
        installedVersionField.setEnabled(true);
        context.register(InstalledVersion.class, (o, arg) -> {
            String text = "";
            if (context.contains(InstalledVersion.class)) {
                text = context.get(InstalledVersion.class).getVersion();
            }
            installedVersionField.setText(text);
        });
        add(installedVersionField, "grow");

        // Row 2
        add(new JLabel("Patch Version"));
        JTextField patchVersionField = new JTextField();
        patchVersionField.setEditable(false);
        patchVersionField.setEnabled(true);
        context.register(PatchFile.class, (o, arg) -> {
            String text = "";
            if (context.contains(PatchFile.class)) {
                text = context.get(PatchFile.class).getVersion();
            }
            patchVersionField.setText(text);
        });
        add(patchVersionField, "grow");
    }
}