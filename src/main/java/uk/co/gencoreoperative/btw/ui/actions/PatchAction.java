package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;

public class PatchAction extends AbstractAction implements Observer {
    private final Context context;

    public PatchAction(Context context) {
        this.context = context;
        context.register(MinecraftHome.class, this);
        context.register(PatchFile.class, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void update(Observable o, Object arg) {
        setEnabled(context.contains(MinecraftHome.class, PatchFile.class));
    }
}
