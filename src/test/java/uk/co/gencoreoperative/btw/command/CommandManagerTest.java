package uk.co.gencoreoperative.btw.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.mock;

import java.util.ArrayList;
import java.util.Collections;
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
        describe("with a CommandManager", () -> {
            final CommandManager manager = new CommandManager();
            describe("and no commands", () -> {
                it("will fail", () -> {
                    try {
                        manager.process(Collections.emptySet());
                        fail();
                    } catch (Exception ignored) {}
                });
            });
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
            describe("and a command that cannot be processed", () -> {
                final Set<AbstractCommand> commands = new HashSet<>();
                commands.add(new SystemCommand<>(mock(ThrowingSupplier.class),"Print String",null, String.class));
                it("throws an error", () -> {
                    try {
                        manager.process(commands);
                        fail();
                    } catch (Exception error) {
                        assertThat(error instanceof CommandManager.MissingInputsException);
                    }
                });
            });
            describe("and a 'cancelled' command in the set", () -> {
                final Set<AbstractCommand> commands = new HashSet<>();
                final List<String> results = new ArrayList<>();
                // Command will not return anything
                final ThrowingSupplier<String> stringSupplier = inputs -> {results.add("first"); return null;};
                commands.add(new UserCommand(stringSupplier, null, "Magic number", String.class));
                // Prints any given String
                final ThrowingSupplier<String> printSupplier = inputs -> {results.add("second"); return "string";};
                commands.add(new SystemCommand<>(printSupplier,"Print String",null, String.class));
                it("will stop processing after the failure", () -> {
                    manager.process(commands);
                    assertThat(results).containsSequence("first");
                });
            });
            describe("and a 'failed' command is in the set", () -> {
                final Set<AbstractCommand> commands = new HashSet<>();
                final List<String> results = new ArrayList<>();
                // Command will not return anything
                final ThrowingSupplier<String> stringSupplier = inputs -> {results.add("first"); throw new Exception("test");};
                commands.add(new SystemCommand<>(stringSupplier, "Magic number", String.class));
                // Prints any given String
                final ThrowingSupplier<String> printSupplier = inputs -> {results.add("second"); return "string";};
                commands.add(new SystemCommand<>(printSupplier,"Print String",null, String.class));
                it("will stop processing after the failure", () -> {
                    manager.process(commands);
                    assertThat(results).containsSequence("first");
                });
            });
            describe("and multiple actions are selected for processing, one is cancelled", () -> {
                final Set<AbstractCommand> commands = new HashSet<>();
                final List<String> results = new ArrayList<>();
                // Command will not return anything
                final ThrowingSupplier<String> stringSupplier1 = inputs -> {results.add("first"); return null;};
                commands.add(new SystemCommand<>(stringSupplier1, "Magic number", String.class));
                final ThrowingSupplier<String> stringSupplier2 = inputs -> {results.add("second"); return "1";};
                commands.add(new SystemCommand<>(stringSupplier2, "Magic number", String.class));

                it("will stop processing after the failure", () -> {
                    manager.process(commands);
                    assertThat(results).containsSequence("first");
                });
            });
        });
    }
}