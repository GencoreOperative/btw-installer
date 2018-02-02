package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;

public class DefaultMinecraftHome extends AbstractAction {
    private final Context context;

    public DefaultMinecraftHome(Context context) {
        this.context = context;
        putValue(Action.NAME, Strings.MENU_DEFAULT_MINECRAFT_HOME.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        context.add(new MinecraftHome(new PathResolver().get()));
    }
}
