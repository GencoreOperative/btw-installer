package uk.co.gencoreoperative.btw.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.forgerock.cuppa.Cuppa.when;
import static uk.co.gencoreoperative.btw.utils.Percentage.*;

import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.junit.CuppaRunner;
import org.junit.runner.RunWith;

@Test
@RunWith(CuppaRunner.class)
public class PercentageTest {
    {
        describe("Percentages", () -> {
            when("#getProgress", () -> {
                it("0 of 2 = 0%", () -> {
                    assertThat(getProgress(0, 2)).isEqualTo(0);
                });
                it("1 of 2 = 50%", () -> {
                    assertThat(getProgress(1, 2)).isEqualTo(50);
                });
                it("2 of 2 = 100%", () -> {
                    assertThat(getProgress(2, 2)).isEqualTo(100);
                });
            });
        });
    }
}