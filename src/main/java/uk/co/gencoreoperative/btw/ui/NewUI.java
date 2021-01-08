package uk.co.gencoreoperative.btw.ui;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.actions.CloseAction;
import uk.co.gencoreoperative.btw.ui.actions.PatchAction;
import uk.co.gencoreoperative.btw.ui.actions.ShowLogAction;
import uk.co.gencoreoperative.btw.ui.panels.AddonsPanel;
import uk.co.gencoreoperative.btw.ui.panels.MinecraftHomePanel;
import uk.co.gencoreoperative.btw.ui.panels.SelectPatchPanel;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.version.Version;
import uk.co.gencoreoperative.btw.version.VersionManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;

import static uk.co.gencoreoperative.btw.ui.ToolTipHelper.withToolTip;

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

        // Center - For main user interaction panels
        add(createCentrePanel(), BorderLayout.CENTER);

        // South - Splits into two sections
        JPanel buttonSouth = new JPanel(new BorderLayout());
        buttonSouth.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // South, West - for help button
        buttonSouth.add(helpButtonsLayout(frame), BorderLayout.WEST);

        // South, Center - for action buttons.
        buttonSouth.add(actionButtons(frame), BorderLayout.CENTER);
        add(buttonSouth, BorderLayout.SOUTH);

        initialiseListenersOnContext();
    }

    private JPanel createCentrePanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, wrap 1, insets 10"));
        panel.add(new SelectPatchPanel(context, dialogFactory), "grow");
        panel.add(new AddonsPanel(context, dialogFactory), "grow");
        panel.add(new MinecraftHomePanel(context, dialogFactory), "grow");
        return panel;
    }

    private JPanel helpButtonsLayout(JFrame frame) {
        FlowLayout layout = new FlowLayout(FlowLayout.LEADING);
        layout.setAlignOnBaseline(true);
        JPanel panel = new JPanel(layout);
        panel.add(new AboutLabel());
        panel.add(withToolTip(new JButton(new ShowLogAction(frame))));
        return panel;
    }

    private JPanel actionButtons(JFrame frame) {
        FlowLayout layout = new FlowLayout(FlowLayout.TRAILING);
        layout.setAlignOnBaseline(true);
        JPanel buttons = new JPanel(layout);
        buttons.add(withToolTip(new JButton(new PatchAction(context, dialogFactory))));
        buttons.add(withToolTip(new JButton(new CloseAction(frame, true))));
        return buttons;
    }

    private void initialiseListenersOnContext() {
        // Installed Version listener - responds to changes in MineCraft Home
        context.register(MinecraftHome.class, (o, arg) -> {
            MinecraftHome home = (MinecraftHome) arg;

            if (home == null) {
                context.remove(InstalledVersion.class);
                if (context.contains(InstalledVersion.class)) {
                    context.remove(InstalledVersion.class);
                }
                return;
            }

            InstalledVersion installedVersion = identifyInstalledVersion(home);
            if (installedVersion == null) {
                context.remove(InstalledVersion.class);
            } else {
                context.add(installedVersion);
            }
        });
    }

    /**
     * Identify based on the Minecraft Home, whether there is an installed version of
     * BetterThanWolves present.
     * <p>
     * This will only identify versions installed by this utility. Other versions will
     * be ignored completely.
     * <p>
     * If there was a problem with reading the version information, we can proceed with
     * the {@link Version#NOT_RECOGNISED} version.
     *
     * @param home Non null home folder.
     * @return {@code null} if no version could be found, otherwise non null.
     */
    private InstalledVersion identifyInstalledVersion(MinecraftHome home) {
        PathResolver pathResolver = new PathResolver(home.getFolder());
        File installedJar = new File(pathResolver.betterThanWolves(), "BetterThanWolves.jar");
        if (!installedJar.exists()) return null;

        VersionManager manager = VersionManager.getVersionManager(pathResolver);
        Version version = manager.getVersion().orElse(Version.NOT_RECOGNISED);
        return new InstalledVersion(installedJar, version);
    }

    public Context getContext() {
        return context;
    }
}
