package com.arrwhi.pbox.client.filesystem;

import com.arrwhi.pbox.msg.Message;

/**
 * Created by arran on 17/01/2017.
 */
public class FileSystemChangeEvent {

    private final Message msg;
    private final DirWatchEvent event;

    public FileSystemChangeEvent(DirWatchEvent event, Message msg) {
        this.event = event;
        this.msg = msg;
    }

    public Message getMsg() {
        return msg;
    }

    public DirWatchEvent getEvent() {
        return event;
    }
}
