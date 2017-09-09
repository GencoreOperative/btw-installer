package uk.co.gencoreoperative.btw;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

public class CommandTest {

    private Command<String> badger;

    @Before
    public void setUp() throws Exception {
        badger = new Command<>(
                () -> "badger",
                string -> string.equals("badger"),
                "its a badger");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldOnlyAllowAccessToResultAfterProcessing() {
        badger.promise().get();
    }

    @Test
    public void shouldIndicateProcessedAfterProcessing() {
        assertThat(badger.isProcessed()).isFalse();
        badger.process();
        assertThat(badger.isProcessed()).isTrue();
    }

    @Test
    public void shouldIndicateSuccessIfSuccessful() {
        badger.process();
        assertThat(badger.isSuccessful()).isTrue();
    }

    @Test
    public void shouldBeNotifiedIfCommandIsProcessed() {
        final AtomicBoolean updated = new AtomicBoolean(false);
        badger.addObserver((o, arg) -> updated.set(true));
        badger.process();
        assertThat(updated.get()).isTrue();
    }
}