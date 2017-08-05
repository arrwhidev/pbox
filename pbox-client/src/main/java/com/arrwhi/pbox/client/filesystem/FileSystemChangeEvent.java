package com.arrwhi.pbox.client.filesystem;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by arran on 17/01/2017.
 */
public class FileSystemChangeEvent {

    private WatchEvent.Kind<?> kind;
    private Path path;

    public FileSystemChangeEvent(WatchEvent.Kind<?> kind, Path path) {
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
