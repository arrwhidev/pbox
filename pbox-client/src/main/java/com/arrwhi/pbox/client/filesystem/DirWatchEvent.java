package com.arrwhi.pbox.client.filesystem;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by arran on 14/01/17.
 */
public class DirWatchEvent {
    public WatchEvent.Kind<?> kind;
    public Path path;
}
