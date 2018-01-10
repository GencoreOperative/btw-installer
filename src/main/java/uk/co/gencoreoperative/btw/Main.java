package uk.co.gencoreoperative.btw;

import com.beust.jcommander.JCommander;
import uk.co.gencoreoperative.btw.command.AbstractCommand;
import uk.co.gencoreoperative.btw.command.CommandManager;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Progress;
import uk.co.gencoreoperative.btw.ui.Strings;

import java.util.Optional;

// Based on  http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=8925

public class Main {

    public static void main(String... args) {
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
