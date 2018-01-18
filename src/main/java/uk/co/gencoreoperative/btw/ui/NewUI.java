package uk.co.gencoreoperative.btw.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.VersionResolver;
import uk.co.gencoreoperative.btw.ui.actions.ChooseMinecraftHome;
import uk.co.gencoreoperative.btw.ui.actions.CloseAction;
import uk.co.gencoreoperative.btw.ui.actions.PatchAction;
import uk.co.gencoreoperative.btw.ui.panels.BTWVersionPanel;
import uk.co.gencoreoperative.btw.ui.panels.MinecraftHomePanel;
import uk.co.gencoreoperative.btw.ui.panels.SelectPatchPanel;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;

public class NewUI extends JPanel {
    private final ActionFactory actionFactory = new ActionFactory(new DialogFactory(this));
    private final Context context = new Context();

    public NewUI(JDialog dialog) {
        setLayout(new BorderLayout());

        JPanel centre = new JPanel(new MigLayout("fillx, wrap 1"));
        centre.add(new MinecraftHomePanel(context, actionFactory), "grow");
        centre.add(new SelectPatchPanel(actionFactory, context), "grow");
        centre.add(new BTWVersionPanel(actionFactory, context), "grow");
        add(centre, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttons.add(new JButton(new PatchAction(context, actionFactory)));
        buttons.add(new JButton(new CloseAction(dialog)));
        add(buttons, BorderLayout.SOUTH);

        File defaultHome = new PathResolver().get();
        if (defaultHome.exists()) {
            context.add(new MinecraftHome(defaultHome));
        }
    }

    public static void main(String... args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }

        JDialog dialog = new JDialog();
        dialog.setTitle(Strings.TITLE_PATCH.getText());
        NewUI ui = new NewUI(dialog);
        dialog.add(ui);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new CloseAction(dialog).actionPerformed(null);
            }
        });

        // Determine the smallest size
        dialog.pack();
        dialog.setMinimumSize(dialog.getPreferredSize());

        // Set a comfortable size
        dialog.setSize(300, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        ChooseMinecraftHome.initaliseMinecraftHome(ui.getContext());
    }

    public Context getContext() {
        return context;
    }
}
