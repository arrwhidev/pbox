package com.arrwhi.pbox.client.filesystem;

import com.arrwhi.pbox.client.index.IndexEntry;
import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by arran on 15/01/17.
 */
public class FileSystemUtils {

    private static final String HASH_ALGORITHM = "SHA-256";

    public static byte[] readBytes(File f) throws IOException {
        return Files.readAllBytes(Paths.get(f.toString()));
    }

    public static String getRelativePath(String rootDir, String absolutePath) {
        Path bPath = Paths.get(rootDir);
        Path absPath = Paths.get(absolutePath);
        return bPath.relativize(absPath).toString();
    }

    public static IndexEntry createIndexEntry(File file) throws Exception {
        String name = file.getName();
        String path = file.getAbsolutePath();
        String hashString = hashToHex(createHash(readBytes(file)));
        return new IndexEntry(name, path, hashString);
    }

    public static byte[] createHash(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(HASH_ALGORITHM).digest(data);
    }

    public static String hashToHex(byte[] hash) throws UnsupportedEncodingException {
        return Hex.encodeHexString(hash);
    }
}
