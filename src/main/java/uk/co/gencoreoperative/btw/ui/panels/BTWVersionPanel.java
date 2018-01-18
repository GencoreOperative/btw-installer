package uk.co.gencoreoperative.btw.ui.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

public class BTWVersionPanel extends JPanel {
    public BTWVersionPanel() {
        setBorder(new TitledBorder("Versions"));
        setLayout(new MigLayout(
                "fillx, wrap 2",
                "[min!][]"));

        // Row 1
        add(new JLabel("Installed Version"));
        JTextField installedVersionField = new JTextField();
        installedVersionField.setEnabled(false);
        add(installedVersionField, "grow");

        // Row 2
        add(new JLabel("Patch Version"));
        JTextField patchVersionField = new JTextField();
        patchVersionField.setEnabled(false);
        add(patchVersionField, "grow");
    }
}