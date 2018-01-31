package uk.co.gencoreoperative.btw.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.it;
import static org.forgerock.cuppa.Cuppa.when;
import static org.junit.Assert.fail;
import static uk.co.gencoreoperative.btw.utils.CheckSumVerifier.validateFileStream;
import static uk.co.gencoreoperative.btw.utils.CheckSumVerifier.validateStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.junit.CuppaRunner;
import org.junit.runner.RunWith;

@Test
@RunWith(CuppaRunner.class)
public class CheckSumVerifierTest {
    {
        when("Verifying a stream", () -> {
            // $ md5 ab.zip
            // MD5 (ab.zip) = dbc3394a4ceafa496d4147cc5927bf49

            InputStream stream = CheckSumVerifier.verifiableStream(
                    "dbc3394a4ceafa496d4147cc5927bf49",
                    CheckSumVerifierTest.class.getResourceAsStream("/ab.zip"));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            it("matches the checksum", () -> {
                FileUtils.copyStream(stream, output, true, true);
            });
        });

        when("Validate a file", () -> {
            File tempFile = null;
            try {
                tempFile = File.createTempFile("tmp", "zip");
                FileUtils.copyStream(
                        CheckSumVerifierTest.class.getResourceAsStream("/ab.zip"),
                        new FileOutputStream(tempFile),
                        true,
                        true);
                tempFile.deleteOnExit();
            } catch (IOException e) {
                fail(e.getMessage());
            }

            File finalTempFile = tempFile;
            it("matches the checksum", () -> {
                boolean result = validateFileStream("dbc3394a4ceafa496d4147cc5927bf49", finalTempFile);
                assertThat(result).isTrue();
            });
        });
        when("Validating a stream", () -> {
            it("matches the checksum", () -> {
                boolean result = validateStream(
                        "dbc3394a4ceafa496d4147cc5927bf49",
                        CheckSumVerifierTest.class.getResourceAsStream("/ab.zip"));
                assertThat(result).isTrue();
            });
        });
    }
}