package uk.co.gencoreoperative.btw.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.afterEach;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.junit.CuppaRunner;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import uk.co.gencoreoperative.btw.utils.ThrowingSupplier;

@Test
@RunWith(CuppaRunner.class)
@SuppressWarnings("unchecked")
public class CommandManagerTest {
    {
        describe("With a CommandManager", () -> {
            final CommandManager manager = new CommandManager();
            describe("and an empty set of commands", () -> {
                final Set<AbstractCommand> commands = new HashSet<>();
                afterEach(commands::clear);

                describe("and some dummy Commands", () -> {
                    final List<ThrowingSupplier> results = new ArrayList<>();
                    // Creates a number as a String
                    ThrowingSupplier<String> stringSupplier = inputs -> {results.add((ThrowingSupplier) this); return null;};
                    commands.add(new SystemCommand<>(stringSupplier, "Magic number", String.class));
                    // Prints any given String
                    ThrowingSupplier<String> printSupplier = inputs -> {results.add((ThrowingSupplier) this); return null;};
                    commands.add(new SystemCommand<>(printSupplier,"Print String",null, String.class));
                    it("are executed in sorted order", () -> {
                        manager.process(commands);
                        assertThat(results).containsSequence(stringSupplier, printSupplier);
                    });
                });
                describe("and a command that cannot be resolved", () -> {
                    commands.add(new SystemCommand<>(mock(ThrowingSupplier.class),"Print String",null, String.class));
                    it("throws an error", () -> {
                        try {
                            manager.process(commands);
                            fail();
                        } catch (Exception ignored) {}
                    });
                });
            });
        });
    }
}