package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.command.AbstractCommand;
import uk.co.gencoreoperative.btw.command.CommandManager;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Progress;
import uk.co.gencoreoperative.btw.ui.Strings;

import java.util.Optional;

// Based on  http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=8925

public class Main {

    private Progress progress;
    private DialogFactory dialogFactory = new DialogFactory(progress);
    private ActionFactory actionFactory = new ActionFactory(dialogFactory);
    private Commands commands = new Commands(actionFactory);

    public Main() {
        // Initialise UI with the commands to visualise
        progress = new Progress(this);
        for (AbstractCommand command : commands.getCommands()) {
            progress.addItem(command);
        }
    }

    public void start() {
        // The commands are organised in a chain, with the last depending on all previous.
        CommandManager manager = new CommandManager();
        try {
            manager.process(commands.getCommands());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        Optional<AbstractCommand> cancelled = commands.getCommands().stream()
                .filter(AbstractCommand::isProcessed)
                .filter(AbstractCommand::isCancelled)
                .findFirst();
        Optional<AbstractCommand> failed = commands.getCommands().stream()
                .filter(AbstractCommand::isProcessed)
                .filter(    c -> !c.isSuccess()).findFirst();
        if (cancelled.isPresent()) {
            dialogFactory.getInformationDialog(Strings.CANCELLED_DETAIL.getText());
        } else if (failed.isPresent()) {
            dialogFactory.getFailedDialog(failed.get());
        } else {
            dialogFactory.getSuccessDialog(Strings.SUCCESS_MSG.getText());
        }
    }

    public static void main(String... args) {
        new Main();
    }
}
