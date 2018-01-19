package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Errors;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.panels.ProgressPanel;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;
import uk.co.gencoreoperative.btw.ui.workers.PatchWorker;

public class PatchAction extends AbstractAction implements Observer {
    private final Context context;
    private final ActionFactory factory;
    private DialogFactory dialogFactory;

    public PatchAction(Context context, ActionFactory factory, DialogFactory dialogFactory) {
        this.context = context;
        this.factory = factory;
        this.dialogFactory = dialogFactory;

        putValue(Action.NAME, Strings.BUTTON_PATCH.getText());

        context.register(MinecraftHome.class, this);
        context.register(PatchFile.class, this);
        update(null, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MinecraftHome minecraftHome = context.get(MinecraftHome.class);
        PatchFile patchFile = context.get(PatchFile.class);

        PathResolver pathResolver = new PathResolver(minecraftHome.getFolder());

        if (!pathResolver.oneFiveTwo().exists()) {
            dialogFactory.failed(Errors.MC_ONE_FIVE_TWO_NOT_FOUND.getReason());
            return;
        }

        ProgressPanel panel = new ProgressPanel();
        PatchWorker worker = new PatchWorker(minecraftHome, patchFile, factory, context, panel);
        worker.execute();
    }

    @Override
    public void update(Observable o, Object arg) {
        setEnabled(context.contains(MinecraftHome.class, PatchFile.class));
    }
}
