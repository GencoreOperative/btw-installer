package uk.co.gencoreoperative.btw.ui;

import static uk.co.gencoreoperative.btw.ui.ToolTipHelper.withToolTip;
import static uk.co.gencoreoperative.btw.utils.OSUtils.setIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Optional;

import net.miginfocom.swing.MigLayout;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.actions.ShowLogAction;
import uk.co.gencoreoperative.btw.version.Version;
import uk.co.gencoreoperative.btw.version.VersionManager;
import uk.co.gencoreoperative.btw.ui.actions.ChooseMinecraftHome;
import uk.co.gencoreoperative.btw.ui.actions.CloseAction;
import uk.co.gencoreoperative.btw.ui.actions.PatchAction;
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
        CloseAction closeAction = new CloseAction(frame, true);
        CloseAction.apply(frame, closeAction);

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
