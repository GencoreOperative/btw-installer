package uk.co.gencoreoperative.btw.utils;

import static java.text.MessageFormat.format;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Responsible for taking byte[] and verifying that it matches a checksum.
 */
public class CheckSumVerifier {

    public static InputStream verifiableStream(String checksum, InputStream inputStream) {
        return new DigestInputStream(inputStream, createDigest()) {
            @Override
            public void close() throws IOException {
                super.close();
                MessageDigest messageDigest = getMessageDigest();
                byte[] digest = messageDigest.digest();
                String hash = byteArrayToHex(digest);
                if (!checksum.equals(hash)) {
                    throw new IOException(format("Checksums did not match:\nRequested: {0}\nActual: {1}", checksum, hash));
                }
            }
        };
    }

    public static boolean validateStream(String checksum, InputStream inputStream) {
        MessageDigest digest = createDigest();
        DigestOutputStream digestStream = new DigestOutputStream(new ByteArrayOutputStream(), digest);
        FileUtils.copyStream(inputStream, digestStream, true, true);
        String hash = byteArrayToHex(digest.digest());
        return hash.equals(checksum);
    }

    public static boolean validateFileStream(String checksum, File file) {
        try {
            return validateStream(checksum, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static MessageDigest createDigest() {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        return md;
    }
}
