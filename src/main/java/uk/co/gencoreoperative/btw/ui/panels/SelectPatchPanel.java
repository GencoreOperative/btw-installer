package uk.co.gencoreoperative.btw.ui.panels;

import static uk.co.gencoreoperative.btw.ui.panels.MinecraftHomePanel.versionPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.actions.ChoosePatch;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;

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
        add(versionPanel(context, PatchFile.class));
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
}