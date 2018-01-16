package uk.co.gencoreoperative.btw.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import java.io.ByteArrayInputStream;
import java.util.zip.ZipEntry;

import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.junit.CuppaRunner;
import org.junit.runner.RunWith;

@Test
@RunWith(CuppaRunner.class)
public class PathAndDataTest {
    {
        describe("A ZipEntry with no data", () -> {
            ZipEntry mockEntry = mock(ZipEntry.class);
            given(mockEntry.isDirectory()).willReturn(true);
            given(mockEntry.getName()).willReturn("/Badger/");

            it("should indicate it is a Directory", () -> {
                PathAndData pathAndData = PathAndData.fromZipEntry(mockEntry, null);
                assertThat(pathAndData.isDirectory()).isTrue();
                assertThat(pathAndData.getPath()).contains("Badger");
            });
        });
        describe("A ZipEntry with data", () -> {
            ZipEntry mockEntry = mock(ZipEntry.class);
            given(mockEntry.isDirectory()).willReturn(false);
            given(mockEntry.getName()).willReturn("/Badger");

            it("should indicate it is a File", () -> {
                PathAndData pathAndData = PathAndData.fromZipEntry(mockEntry, new ByteArrayInputStream("".getBytes()));
                assertThat(pathAndData.isDirectory()).isFalse();
                assertThat(pathAndData.getPath()).contains("Badger");
            });
        });
    }
}