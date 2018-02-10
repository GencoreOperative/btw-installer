package uk.co.gencoreoperative.btw.ui.actions;

import static java.text.MessageFormat.*;
import static uk.co.gencoreoperative.btw.utils.FileUtils.recursiveDelete;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.panels.MinecraftHomePanel;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.utils.FileUtils;
import uk.co.gencoreoperative.btw.utils.Logger;

/**
 * Removes the previously installed patch if one was present.
 *
 * Valid only if a {@link MinecraftHome} signal is present
 * AND a {@link InstalledVersion} signal.
 */
public class RemoveAction extends AbstractAction {
    private Component parent;
    private final Context context;

    public RemoveAction(Component dialog, Context context) {
        this.parent = dialog;
        this.context = context;

        putValue(Action.NAME, Strings.ACTION_REMOVE_PATCH.getText());
        putValue(Action.SMALL_ICON, Icons.BIN_CLOSED.getIcon());

        setEnabled(isApplicable());
        context.register(InstalledVersion.class, (o, arg) -> setEnabled(isApplicable()));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        int response = JOptionPane.showConfirmDialog(
                parent,
                Strings.REMOVE_QUESTION.getText(),
                Strings.TITLE_REMOVE_CONFIRM.getText(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            PathResolver resolver = new PathResolver(context.get(MinecraftHome.class).getFolder());
            if (deleteFolder(resolver.betterThanWolves())) {
                context.remove(InstalledVersion.class);
            }
        }
    }

    private boolean isApplicable() {
        return context.contains(MinecraftHome.class, InstalledVersion.class);
    }

    /**
     * @param folder Non null folder to delete.
     * @return {@code true} if the folder was deleted.
     */
    private boolean deleteFolder(File folder) {
        try {
            recursiveDelete(folder);
        } catch (IOException e) {
            Logger.error(format("Error whilst deleting {0}", folder.getPath()), e);
        }
        return !folder.exists();
    }
}
