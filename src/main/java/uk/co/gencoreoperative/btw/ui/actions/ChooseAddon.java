package uk.co.gencoreoperative.btw.ui.actions;

import uk.co.gencoreoperative.btw.actions.Request;
import uk.co.gencoreoperative.btw.ui.*;
import uk.co.gencoreoperative.btw.ui.signals.AddonFiles;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

public class ChooseAddon extends AbstractAction {
    private final Context context;
    private final DialogFactory dialogFactory;
    private final AddonFiles addonFiles = new AddonFiles();

    public ChooseAddon(Context context, DialogFactory dialogFactory) {
        this.context = context;
        this.dialogFactory = dialogFactory;

        putValue(Action.NAME, Strings.BUTTON_ADD.getText());
        putValue(Action.SHORT_DESCRIPTION, Strings.TOOLTIP_SELECT_ADDON.getText());
        putValue(Action.SMALL_ICON, Icons.ADD.getIcon());

        context.add(addonFiles);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File file = Request.requestAddonZip(dialogFactory);
        if (file == null) return;

        try {
            ZipTreeViewer zipTreeViewer = new ZipTreeViewer();
            zipTreeViewer.view(new ZipFile(file));

            if (!zipTreeViewer.getPathString().toString().equals(Strings.NOT_RECOGNISED.getText())) {
                addonFiles.add(file, zipTreeViewer.getPathString());
                context.add(addonFiles);
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
