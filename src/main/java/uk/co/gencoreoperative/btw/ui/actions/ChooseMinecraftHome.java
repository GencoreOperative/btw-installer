package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.VersionResolver;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;

public class ChooseMinecraftHome extends AbstractAction {
    private final Context context;
    private final ActionFactory factory;

    public ChooseMinecraftHome(Context context, ActionFactory factory) {
        this.context = context;
        this.factory = factory;
        putValue(Action.NAME, Strings.BUTTON_CHANGE_MINECRAFT_HOME.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File file = factory.selectMinecraftHome();
        if (file == null) return;
        initialiseMinecraftHome(new PathResolver(file), context);
    }

    private static void initialiseMinecraftHome(PathResolver resolver, Context context) {
        File homeFolder = resolver.get();
        if (homeFolder.exists()) {
            MinecraftHome value = new MinecraftHome(homeFolder);
            context.add(value);
        }
    }

    public static void initaliseMinecraftHome(Context context) {
        initialiseMinecraftHome(new PathResolver(), context);
    }
}
