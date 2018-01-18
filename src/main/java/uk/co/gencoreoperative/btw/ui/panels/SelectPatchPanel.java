package uk.co.gencoreoperative.btw.ui.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

public class SelectPatchPanel extends JPanel {
    public SelectPatchPanel() {
        setBorder(new TitledBorder("Select Patch"));
        setLayout(new MigLayout(
                "wrap 2, fillx",
                "[][min!]"));

        // Row 1
        JTextField selectPatchField = new JTextField();
        selectPatchField.setEditable(false);
        add(selectPatchField, "grow");
        add(new JButton("Choose"));
    }
}