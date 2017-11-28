package uk.co.gencoreoperative.btw.command;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The role of the {@link CommandManager} is to orchestrate the {@link AbstractCommand}
 * instances that it is provided.
 *
 * It will be responsible for resolving each command and executing them in order to
 * ensure that all pre-requisites are met.
 *
 * If there is a failure during the execution chain, then the error can halt processing
 * and be presented to the caller.
 */
public class CommandManager {
    public void process(Set<AbstractCommand> commands) throws Exception {
        Map<Class, Object> results = new HashMap<>();
        while (!areAllCommandsComplete(commands)) {

            // Find all commands that are not yet processed.
            Set<AbstractCommand> available = getAvailableCommands(commands, results);
            if (available.isEmpty()) throw new NoRemainingCommandsException(commands, results);
            for (AbstractCommand command : available) {
                Optional result = command.process(results);
                if (result.isPresent()) {
                    Object value = result.get();
                    results.put(command.output(), value);
                } else {
                    return;
                }
            }
        }
    }

    private Set<AbstractCommand> getAvailableCommands(Set<AbstractCommand> allCommands, Map<Class, Object> results) {
        return allCommands.stream()
                .filter(c -> !c.isProcessed())
                .filter(c -> results.keySet().containsAll(c.input())).collect(Collectors.toSet());
    }

    private boolean areAllCommandsComplete(Set<AbstractCommand> commands) {
        return commands.stream().allMatch(AbstractCommand::isProcessed);
    }

    /**
     * Indicates that processing of commands by the {@link CommandManager} failed
     * because it was unable to find a suitable command to fulfil the requirements
     * of the remaining commands as yet un-processed.
     */
    public class NoRemainingCommandsException extends RuntimeException {
        public NoRemainingCommandsException(Set<AbstractCommand> commands, Map<Class, Object> results) {
            super(format("Could not find any further commands to execute:\nCommands:\n{0}\nResults:\n{1}",
                    commands.stream().map(AbstractCommand::toString).collect(joining("\n")),
                    results.keySet().stream().map(k -> format("{0}={1}", k, results.get(k))).collect(joining("\n"))));
        }
    }
}
