package com.arrwhi.pbox.client.filesystem;

/**
 * Created by arran on 17/01/2017.
 */
public class FileSystemChangeEvent {

    private final DirWatchEvent event;

    public FileSystemChangeEvent(DirWatchEvent event) {
        this.event = event;
    }

    public DirWatchEvent getEvent() {
        return event;
    }
}
