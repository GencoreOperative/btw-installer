package uk.co.gencoreoperative.btw.command;

import static org.junit.Assert.*;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Test;

public class AbstractCommandTest {

    @Test
    public void shouldYieldResultWhenPromiseTriggered() {
        AbstractCommand<String> command = new AbstractCommand<String>("test") {
            @Override
            protected String processAction() throws Exception {
                return "badger";
            }
        };
        Supplier<Optional<String>> promise = command.promise();
        String result = promise.get().get();
        assertEquals(result, "badger");
        assertTrue(command.isSuccess());
    }

    @Test
    public void shouldReturnNullWhenCancelled() {
        AbstractCommand<String> command = new AbstractCommand<String>("test") {
            @Override
            protected String processAction() throws Exception {
                return null;
            }
        };
        Supplier<Optional<String>> promise = command.promise();
        Optional<String> result = promise.get();
        assertFalse(result.isPresent());
        assertTrue(command.isCancelled());
    }

    @Test
    public void shouldReturnNullWhenFailed() {
        AbstractCommand<String> command = new AbstractCommand<String>("test") {
            @Override
            protected String processAction() throws Exception {
                throw new Exception("Badgers!");
            }
        };
        Supplier<Optional<String>> promise = command.promise();
        Optional<String> result = promise.get();
        assertFalse(result.isPresent());
        assertFalse(command.isSuccess());
        assertTrue(command.getFailedReason() != null);
    }
}