package com.arrwhi.pbox.crypto;

import com.arrwhi.pbox.util.PathHelper;
import com.arrwhi.pbox.util.PropertiesHelper;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by arran on 29/07/17.
 */
public class HashFactory {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static String SOURCE_DIR;

    static {
        SOURCE_DIR = PropertiesHelper.get("sourceDirectory");
    }

    public static String create(File file) throws Exception {
        String relativePath = PathHelper.getRelativePath(SOURCE_DIR, file.getAbsolutePath());

        byte[] payload = file.isDirectory() ? new byte[0] : readBytes(file);
        return create(relativePath, payload);
    }

    public static String create(String path, byte[] payload) throws Exception {
        byte[] pathBytes = path.getBytes(Charsets.UTF_8);
        return create(pathBytes, payload);
    }

    /**
     * hash = toHex(sha256(filePathBytes + fileDataBytes))
     */
    private static String create(byte[] path, byte[] payload) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(path);
        byteArrayOutputStream.write(payload);
        byte[] hash = byteArrayOutputStream.toByteArray();
        return hashToHex(createHash(hash));
    }

    private static byte[] readBytes(File f) throws IOException {
        return Files.readAllBytes(Paths.get(f.toString()));
    }

    private static byte[] createHash(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(HASH_ALGORITHM).digest(data);
    }

    private static String hashToHex(byte[] hash) throws UnsupportedEncodingException {
        return Hex.encodeHexString(hash);
    }
}
