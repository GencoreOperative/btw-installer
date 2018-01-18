package uk.co.gencoreoperative.btw.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.before;
import static org.forgerock.cuppa.Cuppa.beforeEach;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.forgerock.cuppa.Cuppa.when;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.StringJoiner;

import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.junit.CuppaRunner;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

@RunWith(CuppaRunner.class)
@Test
public class ContextTest {
    {
        when("adding a value to the context", () -> {
            it("notifies a listener", () -> {
                Context context = new Context();
                Observer mockListener = mock(Observer.class);
                context.register(String.class, mockListener);
                context.add("badger");
                verify(mockListener).update(any(Observable.class), eq("badger"));
            });
            it("does not notify a listener", () -> {
                Context context = new Context();
                Observer mockListener = mock(Observer.class);
                context.register(String.class, mockListener);
                context.add(1);
                verify(mockListener, times(0)).update(any(Observable.class), any());
            });
        });
        when("removing a value to the context", () -> {
            Context context = new Context();

            Observer mockListener = mock(Observer.class);
            ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

            context.register(String.class, mockListener);
            it("notifies a listener", () -> {
                context.add("badger");
                context.remove("badger");
                verify(mockListener, times(2)).update(any(Observable.class), stringCaptor.capture());
                assertThat(stringCaptor.getAllValues()).containsSequence("badger", null);
            });
        });
        when("getting a value", () -> {

            Context context = new Context();
            context.register(String.class, mock(Observer.class));
            context.add("badger");

            it("returns the value requested", () -> {
                assertThat(context.get(String.class)).isNotNull();
            });
            it("does not return a non-existing value", () -> {
                assertThat(context.get(Integer.class)).isNull();
            });
        });
        when("getting a value with no registered listener", () -> {
            Context context = new Context();
            it("will not return the value", () -> {
                context.add("badger");
                assertThat(context.get(String.class)).isNull();
            });
        });
        when("contains a value", () -> {
            Context context = new Context();
            context.register(String.class, mock(Observer.class));
            context.register(Integer.class, mock(Observer.class));
            context.add("badger");
            context.add(1);
            it("indicates the value is there", () -> {
                assertThat(context.contains(String.class)).isTrue();
            });
            it("indicates the all values are there", () -> {
                assertThat(context.contains(String.class, Integer.class)).isTrue();
            });
            it("indicates the non-existing value is not there", () -> {
                assertThat(context.contains(Map.class)).isFalse();
            });
        });
        when("a user registers a listener, but does not add a value", () -> {
            Context context = new Context();
            context.register(String.class, mock(Observer.class));
            it("does not indicate it contains the value", () -> {
                assertThat(context.contains(String.class)).isFalse();
            });
        });
    }
}