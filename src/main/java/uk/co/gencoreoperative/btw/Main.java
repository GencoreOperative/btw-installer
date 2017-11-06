package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.command.AbstractCommand;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Progress;
import uk.co.gencoreoperative.btw.ui.Strings;

import java.util.Arrays;

// Based on  http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=8925

public class Main {

    private Progress progress;
    private DialogFactory dialogFactory = new DialogFactory(progress);
    private ActionFactory actionFactory = new ActionFactory(dialogFactory);
    private Commands commands = new Commands(actionFactory);

    public Main() {
        progress = new Progress(this);
        for (AbstractCommand command : commands.getCommands()) {
            progress.addItem(command);
        }
    }

    public void start() {
        boolean complete = true;

        commands.getLastCommand().promise().get();

        // Signal the user that all tasks are complete.
        if (complete) {
            dialogFactory.getSuccessDialog(Strings.SUCCESS_TITLE.getText(), Strings.SUCCESS_MSG.getText());
        } else {
            // TODO signal error and reason.
        }
    }

    public static void main(String... args) {
        new Main();
    }
}
