package uk.co.gencoreoperative.btw.ui.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;
import uk.co.gencoreoperative.btw.ui.signals.Versioned;

public class BTWVersionPanel extends JPanel {
    public BTWVersionPanel(Context context) {
        setBorder(new TitledBorder("Versions"));
        setLayout(new MigLayout(
                "fillx, wrap 2",
                "[min!][]"));

        // Row 1
        add(new JLabel("Installed Version"));
        JTextField installedVersionField = new JTextField();
        installedVersionField.setEditable(false);
        installedVersionField.setEnabled(true);
        register(context, InstalledVersion.class, installedVersionField);
        add(installedVersionField, "grow");

        // Row 2
        add(new JLabel("Patch Version"));
        JTextField patchVersionField = new JTextField();
        patchVersionField.setEditable(false);
        patchVersionField.setEnabled(true);
        register(context, PatchFile.class, patchVersionField);
        add(patchVersionField, "grow");
    }

    private void register(Context context, Class<? extends Versioned> type, JTextField field) {
        context.register(type, (o, arg) -> {
            String text = "";
            if (context.contains(type)) {
                text = context.get(type).getVersion();
            }
            field.setText(text);
        });
    }
}