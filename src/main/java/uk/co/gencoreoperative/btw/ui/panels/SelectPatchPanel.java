package uk.co.gencoreoperative.btw.ui.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.actions.ChoosePatch;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;

public class SelectPatchPanel extends JPanel {
    private static final ImageIcon COMPRESS = new ImageIcon(MinecraftHomePanel.class.getResource("/icons/compress.png"));

    public SelectPatchPanel(ActionFactory factory, Context context) {
        setBorder(new TitledBorder("Better Than Wolves Patch"));
        setLayout(new MigLayout(
                "fillx",
                "[min!][][min!]"));

        // Row 1
        final JLabel folderIcon = new JLabel(COMPRESS);
        add(folderIcon);
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
        add(selectPatchField, "grow");
        add(new JButton(new ChoosePatch(factory, context)));
    }
}