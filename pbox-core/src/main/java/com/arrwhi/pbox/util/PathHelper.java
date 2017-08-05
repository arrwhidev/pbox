package com.arrwhi.pbox.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by arran on 29/07/17.
 */
public class PathHelper {

    private PathHelper() {}

    public static String getRelativePath(String rootDir, String absolutePath) {
        Path bPath = Paths.get(rootDir);
        Path absPath = Paths.get(absolutePath);
        return bPath.relativize(absPath).toString();
    }
}
