package uk.co.gencoreoperative.btw.ui.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.actions.ChoosePatch;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;

public class SelectPatchPanel extends JPanel {
    public SelectPatchPanel(ActionFactory factory, Context context) {
        setBorder(new TitledBorder("Select Patch"));
        setLayout(new MigLayout(
                "wrap 2, fillx",
                "[][min!]"));

        // Row 1
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