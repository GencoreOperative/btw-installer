package uk.co.gencoreoperative.btw.utils;

import static org.forgerock.cuppa.Cuppa.it;
import static org.forgerock.cuppa.Cuppa.when;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.junit.CuppaRunner;
import org.junit.runner.RunWith;

@Test
@RunWith(CuppaRunner.class)
public class CheckSumVerifierTest {
    {
        when("Verifying a stream zip file", () -> {
            // $ md5 ab.zip
            // MD5 (ab.zip) = dbc3394a4ceafa496d4147cc5927bf49

            InputStream stream = CheckSumVerifier.verifyStream(
                    "dbc3394a4ceafa496d4147cc5927bf49",
                    CheckSumVerifierTest.class.getResourceAsStream("/ab.zip"));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            it("matches the checksum", () -> {
                FileUtils.copyStream(stream, output, true, true);
            });
        });
    }
}