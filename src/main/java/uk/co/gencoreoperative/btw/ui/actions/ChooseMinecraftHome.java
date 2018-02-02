package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.actions.Request;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;

public class ChooseMinecraftHome extends AbstractAction {
    private final Context context;
    private final DialogFactory dialogFactory;

    public ChooseMinecraftHome(Context context, DialogFactory dialogFactory) {
        this.context = context;
        this.dialogFactory = dialogFactory;
        putValue(Action.NAME, Strings.MENU_CHANGE_MINECRAFT_HOME.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File file = Request.requestMinecraftHome(dialogFactory);
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

    public static JPopupMenu getMinecraftHomeMenu(Context context, DialogFactory dialogFactory) {
        JPopupMenu menu = new JPopupMenu();
        menu.add(new JMenuItem(new DefaultMinecraftHome(context)));
        menu.add(new JMenuItem(new ChooseMinecraftHome(context, dialogFactory)));
        return menu;
    }
}
