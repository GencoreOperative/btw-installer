package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.VersionResolver;
import uk.co.gencoreoperative.btw.actions.Request;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;

public class ChoosePatch extends AbstractAction {
    private final Context context;
    private final DialogFactory dialogFactory;

    public ChoosePatch(Context context, DialogFactory dialogFactory) {
        this.context = context;
        this.dialogFactory = dialogFactory;

        putValue(Action.NAME, Strings.BUTTON_CHOOSE_PATCH.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File file = Request.requestPatchZip(dialogFactory);
        if (file == null) return;

        PatchFile patchFile = new PatchFile(file);

        // If we can detect the patch version, apply this to the PatchFile.
        VersionResolver versionResolver = new VersionResolver();
        String version = versionResolver.extractVersionFromPatch(patchFile.getFile());
        if (version != null) {
            patchFile.setVersion(version);
        }

        context.add(patchFile);
    }
}
