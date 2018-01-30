package uk.co.gencoreoperative.btw.ui;

import static uk.co.gencoreoperative.btw.utils.OSUtils.setIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.VersionResolver;
import uk.co.gencoreoperative.btw.ui.actions.ChooseMinecraftHome;
import uk.co.gencoreoperative.btw.ui.actions.CloseAction;
import uk.co.gencoreoperative.btw.ui.actions.PatchAction;
import uk.co.gencoreoperative.btw.ui.actions.RemoveAction;
import uk.co.gencoreoperative.btw.ui.panels.MinecraftHomePanel;
import uk.co.gencoreoperative.btw.ui.panels.SelectPatchPanel;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.utils.OSUtils;

/**
 * Updated user interface to better capture the details required from the user in order
 * to complete the patching process.
 */
public class NewUI extends JPanel {
    public final DialogFactory dialogFactory;
    private final Context context = new Context();

    public NewUI(JFrame frame) {
        dialogFactory = new DialogFactory(frame);

        setLayout(new BorderLayout(0, 0));

        // Center
        JPanel centre = new JPanel(new MigLayout("fillx, wrap 1, insets 10"));
        centre.add(new MinecraftHomePanel(context, dialogFactory), "grow");
        centre.add(new SelectPatchPanel(context, dialogFactory), "grow");
        add(centre, BorderLayout.CENTER);

        // South - Splits into two sections
        JPanel buttonSouth = new JPanel(new BorderLayout());
        buttonSouth.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // South, West - for help button
        FlowLayout southWestLayout = new FlowLayout(FlowLayout.LEADING);
        southWestLayout.setAlignOnBaseline(true);
        JPanel helpPanel = new JPanel(southWestLayout);
        helpPanel.add(new AboutLabel());
        buttonSouth.add(helpPanel, BorderLayout.WEST);

        // South, Center - for action buttons.
        FlowLayout southCenterLayout = new FlowLayout(FlowLayout.TRAILING);
        southCenterLayout.setAlignOnBaseline(true);
        JPanel buttons = new JPanel(southCenterLayout);
        buttons.add(new JButton(new PatchAction(context, dialogFactory)));
        buttons.add(new JButton(new RemoveAction(frame, context)));
        buttons.add(new JButton(new CloseAction(frame)));
        buttonSouth.add(buttons, BorderLayout.CENTER);
        add(buttonSouth, BorderLayout.SOUTH);

        initialiseListenersOnContext();
    }

    private void initialiseListenersOnContext() {
        // Installed Version listener - responds to changes in MineCraft Home
        context.register(MinecraftHome.class, new Observer() {
            final VersionResolver versionResolver = new VersionResolver();

            @Override
            public void update(Observable o, Object arg) {
                MinecraftHome home = context.get(MinecraftHome.class);
                PathResolver pathResolver = new PathResolver(home.getFolder());

                File installedFolder = pathResolver.betterThanWolves();
                File installedJar = new File(installedFolder, "BetterThanWolves.jar");

                if (installedFolder.exists() && installedJar.exists()) {
                    InstalledVersion version = new InstalledVersion(installedJar);
                    version.setVersion(versionResolver.readVersion(installedFolder));
                    context.add(version);
                } else {
                    context.remove(InstalledVersion.class);
                }
            }
        });
    }

    public static void main(String... args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }

        JFrame frame = new JFrame();
        frame.setTitle(Strings.TITLE_PATCH.getText());

        Image squid = Icons.SQUID.getIcon().getImage();
        frame.setIconImage(squid);
        // MacOS Specific Dock Icon
        if (OSUtils.isMacOS()) {
            setIcon(squid);
        }

        NewUI ui = new NewUI(frame);
        frame.add(ui);
        frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new CloseAction(frame).actionPerformed(null);
            }
        });

        // Determine the smallest size
        frame.pack();
        frame.setMinimumSize(frame.getPreferredSize());

        // Location by OS default, center
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        ChooseMinecraftHome.initaliseMinecraftHome(ui.getContext());
    }

    public Context getContext() {
        return context;
    }
}
