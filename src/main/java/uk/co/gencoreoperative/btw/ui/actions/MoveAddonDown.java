package uk.co.gencoreoperative.btw.ui.actions;

import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.signals.AddonFiles;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MoveAddonDown extends AbstractAction {
    private final Context context;
    private final DialogFactory dialogFactory;

    private int selectedIndex;

    public MoveAddonDown(Context context, DialogFactory dialogFactory) {
        this.context = context;
        this.dialogFactory = dialogFactory;

        putValue(Action.NAME, Strings.BUTTON_DOWN.getText());
        putValue(Action.SHORT_DESCRIPTION, Strings.TOOLTIP_ADDON_DOWN.getText());
        putValue(Action.SMALL_ICON, Icons.ARROW_DOWN.getIcon());
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
        this.setEnabled(index < AddonFiles.getAddons().size() - 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AddonFiles addonFiles = context.get(AddonFiles.class);

        addonFiles.moveDown(selectedIndex);
        context.add(addonFiles);
    }
}
