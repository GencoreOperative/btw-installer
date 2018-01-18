package uk.co.gencoreoperative.btw.ui.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

public class MinecraftHomePanel extends JPanel {
    public MinecraftHomePanel() {
        setBorder(new TitledBorder("Minecraft Home"));
        setLayout(new MigLayout(
                "fillx",
                "[][min!]"));

        // Row 1
        JTextField homeTextField = new JTextField();
        homeTextField.setEnabled(false);
        homeTextField.setEditable(false);
        add(homeTextField, "grow");
        add(new JButton("Change"), "wrap");
    }
}