package uk.co.gencoreoperative.btw.command;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Test;

public class AbstractCommandTest {

    @Test
    public void shouldYieldResultWhenPromiseTriggered() throws Exception {
        AbstractCommand<String> command = new AbstractCommand<String>("test", null) {
            @Override
            protected String processAction(Map<Class, Object> inputs) throws Exception {
                return "badger";
            }

            @Override
            protected boolean canCancel() {
                return false;
            }
        };
        Optional<String> result = command.process(Collections.emptyMap());
        assertEquals(result.get(), "badger");
        assertTrue(command.isSuccess());
    }

    @Test
    public void shouldReturnNullWhenCancelled() throws Exception {
        AbstractCommand<String> command = new AbstractCommand<String>("test", null) {
            @Override
            protected String processAction(Map<Class, Object> inputs) throws Exception {
                return null;
            }

            @Override
            protected boolean canCancel() {
                return true;
            }
        };
        Optional<String> result = command.process(Collections.emptyMap());
        assertFalse(result.isPresent());
        assertTrue(command.isCancelled());
    }

    @Test
    public void shouldIndicateFailedWhenCancelled() throws Exception {
        AbstractCommand<String> command = new AbstractCommand<String>("test", null) {
            @Override
            protected String processAction(Map<Class, Object> inputs) throws Exception {
                return null;
            }

            @Override
            protected boolean canCancel() {
                return true;
            }
        };
        command.process(Collections.emptyMap());
        assertFalse(command.isSuccess());
    }

    @Test
    public void shouldIndicateSuccessWhenNotCapableOfCancel() {
        AbstractCommand<String> command = new AbstractCommand<String>("test", null) {
            @Override
            protected String processAction(Map<Class, Object> inputs) throws Exception {
                return null;
            }

            @Override
            protected boolean canCancel() {
                return false;
            }
        };
        command.process(Collections.emptyMap());
        assertTrue(command.isSuccess());
    }

    @Test
    public void shouldReturnNullWhenFailed() throws Exception {
        AbstractCommand<String> command = new AbstractCommand<String>("test", null) {
            @Override
            protected String processAction(Map<Class, Object> inputs) throws Exception {
                throw new Exception("Badgers!");
            }

            @Override
            protected boolean canCancel() {
                return false;
            }
        };
        Optional<String> result = command.process(Collections.emptyMap());
        assertFalse(result.isPresent());
        assertFalse(command.isSuccess());
        assertTrue(command.getFailedReason() != null);
    }
}