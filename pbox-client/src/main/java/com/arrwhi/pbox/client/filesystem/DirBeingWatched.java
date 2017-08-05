package com.arrwhi.pbox.client.filesystem;

import java.nio.file.Path;
import java.nio.file.WatchKey;

/**
 * Created by arran on 14/01/17.
 */
public class DirBeingWatched {

    private WatchKey key;
    private Path path;

    public DirBeingWatched(WatchKey key, Path path) {
        this.key = key;
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public WatchKey getKey() {
        return key;
    }
}
