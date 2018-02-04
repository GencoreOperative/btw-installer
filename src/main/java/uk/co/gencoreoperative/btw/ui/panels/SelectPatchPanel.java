package uk.co.gencoreoperative.btw.ui.panels;


import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.actions.ChoosePatch;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;
import uk.co.gencoreoperative.btw.version.Version;

public class SelectPatchPanel extends JPanel {
    private final Context context;
    private final DialogFactory dialogFactory;

    public SelectPatchPanel(Context context, DialogFactory dialogFactory) {
        this.context = context;
        this.dialogFactory = dialogFactory;
        setBorder(new TitledBorder("Better Than Wolves Patch"));
        setLayout(new MigLayout(
                "fillx, insets 10, wrap 1"));

        add(selectPatch());
        add(versionPanel(context));
    }

    private JPanel selectPatch() {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 0",
                "[min!][][min!]"));

        // Row 1
        final JLabel folderIcon = new JLabel(Icons.COMPRESS.getIcon());
        panel.add(folderIcon);
        final JTextField selectPatchField = new JTextField(20);
        selectPatchField.setEditable(false);
        selectPatchField.setEnabled(true);
        context.register(PatchFile.class, (o, arg) -> {
            String text = "";
            if (context.contains(PatchFile.class)) {
                text = context.get(PatchFile.class).getFile().getAbsolutePath();
            }
            selectPatchField.setText(text);
        });
        panel.add(selectPatchField, "grow");
        panel.add(new JButton(new ChoosePatch(context, dialogFactory)));
        return panel;
    }

    private JPanel versionPanel(Context context) {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 0",
                "[min!][grow][min!]"));
        panel.add(new JLabel("Version:"));

        JLabel versionLabel = new JLabel();
        versionLabel.setFont(versionLabel.getFont().deriveFont(Font.ITALIC));
        versionLabel.setEnabled(false);
        context.register(PatchFile.class, (o, arg) -> {
            String text = "";
            if (context.contains(PatchFile.class)) {
                text = context.get(PatchFile.class).getPatchVersion();
            }
            versionLabel.setText(text);
        });

        panel.add(versionLabel);
        return panel;
    }
}