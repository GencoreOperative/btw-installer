package uk.co.gencoreoperative.btw.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;

public class FileUtilsTest {
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
                    new FileInputStream(tempA),
                    new FileOutputStream(tempB),
                    true,
                    true);
        });

        System.out.println(tempA.getPath());
        System.out.println(tempA.length());
    }
}