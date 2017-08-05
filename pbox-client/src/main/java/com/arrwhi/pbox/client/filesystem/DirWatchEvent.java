package com.arrwhi.pbox.client.filesystem;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by arran on 14/01/17.
 */
public class DirWatchEvent {

    private WatchEvent.Kind<?> kind;
    private Path path;

    public DirWatchEvent(WatchEvent.Kind<?> kind, Path path) {
        this.kind = kind;
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public WatchEvent.Kind<?> getKind() {
        return kind;
    }
}
