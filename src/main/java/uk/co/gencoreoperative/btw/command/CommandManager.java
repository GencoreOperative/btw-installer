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
 * instances that it is provided with.
 *
 * It will be responsible for resolving each command and executing them in an order that
 * ensures all pre-requisites are met.
 *
 * If there is a failure during the execution chain, then the {@link CommandManager} will
 * halt execution at this point.
 *
 * *Note:* There is no guarantee over the over in which the commands will be executed, other
 * than to follow the dependency graph between commands.
 */
public class CommandManager {

    /**
     * Execute the commands as provided in the set.
     *
     * Start with the commands that have no input requirements, these will generate
     * values as output. Then iterate over the available non-processed commands looking
     * to meet the dependencies of those commands by executing all subsequently
     * available commands.
     *
     * *Note:* If processing encounters a command that fails to execute, then execution
     * is halted at this point.
     *
     * @param commands Non null, non empty set of {@link AbstractCommand} to execute.
     *
     * @throws Exception If the commands provided were empty, or if there was an error
     * with the dependency graph between commands in the set.
     */
    public void process(Set<AbstractCommand> commands) throws Exception {
        if (commands == null || commands.isEmpty()) throw new NullPointerException("empty commands");

        Map<Class, Object> results = new HashMap<>();
        while (!areAllCommandsComplete(commands)) {

            // Find all commands that are not yet processed.
            Set<AbstractCommand> available = getAvailableCommands(commands, results);
            if (available.isEmpty()) throw new MissingInputsException(commands, results);
            for (AbstractCommand command : available) {
                Optional result = command.process(results);
                // Only store a result if the command declares a result.
                if (result.isPresent() && command.output() != null) {
                    Object value = result.get();
                    results.put(command.output(), value);
                }
                // Now check for failure
                long failed = commands.stream()
                        .filter(AbstractCommand::isProcessed)
                        .filter(c -> !c.isSuccess()).count();
                if (failed > 0) return;
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
    public class MissingInputsException extends RuntimeException {
        public MissingInputsException(Set<AbstractCommand> commands, Map<Class, Object> results) {
            super(format("Could not find any inputs to process remaining commands:\nCommands:\n{0}\nResults:\n{1}",
                    commands.stream().map(AbstractCommand::toString).collect(joining("\n")),
                    results.keySet().stream().map(k -> format("\"{0}\"=\"{1}\"", k, results.get(k))).collect(joining("\n"))));
        }
    }
}
