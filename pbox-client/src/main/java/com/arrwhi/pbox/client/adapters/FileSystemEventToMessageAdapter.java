package com.arrwhi.pbox.client.adapters;

import com.arrwhi.pbox.client.filesystem.DirWatchEvent;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.msg.MessageFactory;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by arran on 10/07/16.
 */
public class FileSystemEventToMessageAdapter {

    private final String rootDir;

    public FileSystemEventToMessageAdapter(String rootDir) {
        this.rootDir = rootDir;
    }

    public Message adapt(DirWatchEvent ev) {
        Path p = ev.getPath();
        WatchEvent.Kind<?> kind = ev.getKind();
        if(kind == ENTRY_CREATE) {
            return MessageFactory.createTransportFileMessage(p.toFile(), rootDir);
        } else if(kind == ENTRY_MODIFY) {
            return MessageFactory.createModifyFileMessage(p.toFile(), rootDir);
        } else if(kind == ENTRY_DELETE) {
            return MessageFactory.createDeleteFileMessage(p.toFile(), rootDir);
        }

        return null;
    }
}
