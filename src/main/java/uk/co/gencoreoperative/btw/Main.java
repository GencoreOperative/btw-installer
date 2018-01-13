package uk.co.gencoreoperative.btw;

import javax.swing.*;

import com.beust.jcommander.JCommander;
import uk.co.gencoreoperative.btw.ui.Progress;

// Based on  http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=8925

public class Main {

    public static void main(String... args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) { }

        Installer installer = new Installer();

        JCommander commander = new JCommander(installer);
        commander.parse(args);

        if (installer.isHelp()) {
            commander.usage();
            System.exit(0);
        }

        Progress progress = installer.getUserInterface();
        progress.setCommands(installer.getCommands());
        progress.start();
    }
}
