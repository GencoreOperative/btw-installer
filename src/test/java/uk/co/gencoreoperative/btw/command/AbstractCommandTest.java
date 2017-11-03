package uk.co.gencoreoperative.btw.command;

import static org.junit.Assert.*;

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
        Supplier<String> promise = command.promise();
        assertEquals(promise.get(), "badger");
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
        Supplier<String> promise = command.promise();
        assertNull(promise.get());
        assertTrue(command.isCancelled());
    }

    @Test
    public void shouldReturnNullWhenFailed() {
        AbstractCommand<String> command = new AbstractCommand<String>("test") {
            @Override
            protected String processAction() throws Exception {
                throw new Exception();
            }
        };
        Supplier<String> promise = command.promise();
        assertNull(promise.get());
        assertTrue(command.isCancelled());
    }
}