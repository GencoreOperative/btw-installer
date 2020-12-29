package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.ui.Icons;
import uk.co.gencoreoperative.btw.ui.NewUI;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.actions.ChooseMinecraftHome;
import uk.co.gencoreoperative.btw.ui.actions.CloseAction;
import uk.co.gencoreoperative.btw.utils.OSUtils;
import uk.co.gencoreoperative.btw.utils.os.AppleUtils;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Image;

/**
 * Responsible for starting the user interface.
 * <p>
 * TODO: We may consider supporting command line arguments.
 */
public class Main {
    public static void main(String... args) {
        // Apple title should be set before AWT classes are loaded
        if (OSUtils.isMacOS()) {
            AppleUtils.setTitle(Strings.TITLE.getText());
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }

        JFrame frame = new JFrame();
        frame.setTitle(Strings.TITLE_VERSION.getText());
        Image squid = Icons.SQUID.getIcon().getImage();
        frame.setIconImage(squid);
        // MacOS Specific Dock Icon
        if (OSUtils.isMacOS()) {
            AppleUtils.setDockIcon(squid);
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
}
