package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import uk.co.gencoreoperative.btw.ui.Icons;

public class MinecraftHomeMenuAction extends AbstractAction {
    private final JPopupMenu menu;

    public MinecraftHomeMenuAction(JPopupMenu menu) {
        this.menu = menu;
        putValue(Action.SMALL_ICON, Icons.COG.getIcon());
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Component invoker = menu.getInvoker();
        menu.show(invoker, invoker.getWidth(), 0);
    }
}
