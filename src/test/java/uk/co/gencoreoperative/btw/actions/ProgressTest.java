package uk.co.gencoreoperative.btw.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.forgerock.cuppa.Cuppa.when;

import java.util.ArrayList;
import java.util.List;

import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.junit.CuppaRunner;
import org.junit.runner.RunWith;

@Test
@RunWith(CuppaRunner.class)
public class ProgressTest {
    {
        describe("notification tests", () -> {
            when("progress is added", () -> {
                final Progress progress = new Progress(10);
                it("notifies listeners", () -> {
                    final List<Integer> listenerResults = new ArrayList<>();
                    progress.addObserver((o, arg) -> {
                        Progress p = (Progress) arg;
                        listenerResults.add(p.getProgressPercentage());
                    });
                    progress.addProgress(5);
                    assertThat(listenerResults).contains(50);
                });
            });
        });
    }
}