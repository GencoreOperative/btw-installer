package uk.co.gencoreoperative.btw.actions;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;

import uk.co.gencoreoperative.btw.utils.FileUtils;
import uk.co.gencoreoperative.btw.utils.Timer;

public class ProgressInputStreamTest {
    public static void main(String... args) throws Exception {
        File tempA = File.createTempFile("testA", "ints");
        File tempB = File.createTempFile("testB", "ints");
        DataOutputStream stream = new DataOutputStream(new FileOutputStream(tempA));
        IntStream.range(0, 1_000_000).forEach(i -> {
            try {
                stream.write(i);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
        stream.flush();
        stream.close();

        Timer.time("Copying a 1m byte file", () -> {
            FileUtils.copyStream(
                    new ProgressInputStream(new FileInputStream(tempA), 1_000_000, System.out::println),
                    new FileOutputStream(tempB),
                    true,
                    true);
        });

        System.out.println(tempA.getPath());
        System.out.println(tempA.length());
    }
}