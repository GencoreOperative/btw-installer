package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.panels.MinecraftHomePanel;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;

public class RemoveAction extends AbstractAction {
    private JDialog dialog;
    private final Context context;
    private final ActionFactory actionFactory;

    public RemoveAction(JDialog dialog, Context context, ActionFactory actionFactory) {
        this.dialog = dialog;
        this.context = context;
        this.actionFactory = actionFactory;

        putValue(Action.NAME, Strings.BUTTON_REMOVE.getText());
        putValue(Action.SMALL_ICON, new ImageIcon(MinecraftHomePanel.class.getResource("/icons/bin_closed.png")));

        setEnabled(isApplicable());
        context.register(InstalledVersion.class, (o, arg) -> setEnabled(isApplicable()));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        int response = JOptionPane.showConfirmDialog(
                dialog,
                Strings.REMOVE_QUESTION.getText(),
                Strings.TITLE_REMOVE_CONFIRM.getText(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            PathResolver resolver = new PathResolver(context.get(MinecraftHome.class).getFolder());
            File folder = actionFactory.removePreviousInstallation(resolver);
            if (folder.exists()) {

            } else {
                context.remove(InstalledVersion.class);
            }
        }
    }

    private boolean isApplicable() {
        return context.contains(MinecraftHome.class, InstalledVersion.class);
    }
}
