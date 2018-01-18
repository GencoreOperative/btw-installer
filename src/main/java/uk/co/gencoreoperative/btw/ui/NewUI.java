package uk.co.gencoreoperative.btw.ui;

import javax.swing.*;
import java.awt.*;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ui.panels.BTWVersionPanel;
import uk.co.gencoreoperative.btw.ui.panels.MinecraftHomePanel;
import uk.co.gencoreoperative.btw.ui.panels.SelectPatchPanel;

public class NewUI extends JPanel {
    public NewUI() {
        setLayout(new BorderLayout());

        JPanel centre = new JPanel(new MigLayout("fillx, wrap 1"));
        centre.add(new MinecraftHomePanel(), "grow");
        centre.add(new SelectPatchPanel(), "grow");
        centre.add(new BTWVersionPanel(), "grow");
        add(centre, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttons.add(new JButton("Patch"));
        buttons.add(new JButton("Close"));
        add(buttons, BorderLayout.SOUTH);
    }

    public static void main(String... args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) { }


        JDialog dialog = new JDialog();
        dialog.add(new NewUI());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Determine the smallest size
        dialog.pack();
        dialog.setMinimumSize(dialog.getPreferredSize());

        // Set a comfortable size
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
