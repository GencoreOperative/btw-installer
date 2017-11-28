package uk.co.gencoreoperative.btw.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.junit.CuppaRunner;
import org.junit.runner.RunWith;
import uk.co.gencoreoperative.btw.utils.ThrowingSupplier;

@Test
@RunWith(CuppaRunner.class)
@SuppressWarnings("unchecked")
public class CommandManagerTest {
    {
        describe("With a CommandManager", () -> {
            final CommandManager manager = new CommandManager();
            describe("and some dummy Commands", () -> {
                final Set<AbstractCommand> commands = new HashSet<>();
                final List<String> results = new ArrayList<>();
                // Creates a number as a String
                final ThrowingSupplier<String> stringSupplier = inputs -> {results.add("first"); return "string";};
                commands.add(new SystemCommand<>(stringSupplier, "Magic number", String.class));
                // Prints any given String
                final ThrowingSupplier<String> printSupplier = inputs -> {results.add("second"); return "string";};
                commands.add(new SystemCommand<>(printSupplier,"Print String",null, String.class));
                it("are executed in sorted order", () -> {
                    manager.process(commands);
                    assertThat(results).containsSequence("first", "second");
                });
                it("are executed only once", () -> {
                    manager.process(commands);
                    assertThat(results.size()).isEqualTo(2);
                });
            });
            describe("and a command that cannot be resolved", () -> {
                final Set<AbstractCommand> commands = new HashSet<>();
                commands.add(new SystemCommand<>(mock(ThrowingSupplier.class),"Print String",null, String.class));
                it("throws an error", () -> {
                    try {
                        manager.process(commands);
                        fail();
                    } catch (Exception ignored) {}
                });
            });
            describe("And a command that cannot be processed", () -> {
                final Set<AbstractCommand> commands = new HashSet<>();
                commands.add(new SystemCommand<>(mock(ThrowingSupplier.class),"Print String",null, String.class));
                it("throws an error", () -> {
                    try {
                        manager.process(commands);
                        fail();
                    } catch (Exception error) {
                        assertThat(error instanceof CommandManager.NoRemainingCommandsException);
                    }
                });
            });
            describe("and a failing command in the set", () -> {
                final Set<AbstractCommand> commands = new HashSet<>();
                final List<String> results = new ArrayList<>();
                // Creates a number as a String
                final ThrowingSupplier<String> stringSupplier = inputs -> {results.add("first"); return null;};
                commands.add(new SystemCommand<>(stringSupplier, "Magic number", String.class));
                // Prints any given String
                final ThrowingSupplier<String> printSupplier = inputs -> {results.add("second"); return "string";};
                commands.add(new SystemCommand<>(printSupplier,"Print String",null, String.class));
                it("will stop processing after the failure", () -> {
                    manager.process(commands);
                    assertThat(results).containsSequence("first");
                });
            });
        });
    }
}