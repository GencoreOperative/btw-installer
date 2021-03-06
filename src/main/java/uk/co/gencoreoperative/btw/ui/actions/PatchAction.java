package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.panels.LogPanel;
import uk.co.gencoreoperative.btw.ui.panels.ProgressPanel;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;
import uk.co.gencoreoperative.btw.ui.workers.PatchWorker;

/**
 * The role of {@link PatchAction} is to act as a final gate the user needs
 * to pass through to complete the installation. Once they have selected
 * a {@link MinecraftHome} and a {@link PatchFile} then they can proceed
 * to trigger this action.
 */
public class PatchAction extends AbstractAction implements Observer {
    private final Context context;
    private final ActionFactory factory = new ActionFactory();
    private DialogFactory dialogFactory;

    public PatchAction(Context context, DialogFactory dialogFactory) {
        this.context = context;
        this.dialogFactory = dialogFactory;

        putValue(Action.NAME, Strings.BUTTON_PATCH.getText());
        putValue(Action.SMALL_ICON, Icons.ARROW_RIGHT.getIcon());
        putValue(Action.SHORT_DESCRIPTION, Strings.TOOLTIP_PATCH.getText());

        context.register(MinecraftHome.class, this);
        context.register(PatchFile.class, this);
        update(null, null);
    }


    /**
     * Retrieve the prerequisites from the {@link Context} and trigger the
     * specific Progress dialog and worker needed for patching.
     *
     * @param e Ignored.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        MinecraftHome minecraftHome = context.get(MinecraftHome.class);
        PatchFile patchFile = context.get(PatchFile.class);

        ProgressPanel panel = new ProgressPanel();
        PatchWorker worker = new PatchWorker(minecraftHome, patchFile, factory, context, panel, dialogFactory);
        worker.addPropertyChangeListener(evt -> panel.setProgress(worker.getProgress()));

        worker.execute();
        LogPanel.show(panel, dialogFactory.getParentFrame());
    }

    /**
     * Action is only enabled when both items are present in the context.
     * @param o Ignored
     * @param arg Ignored
     */
    @Override
    public void update(Observable o, Object arg) {
        setEnabled(context.contains(MinecraftHome.class, PatchFile.class));
    }
}
