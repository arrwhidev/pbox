package com.arrwhi.pbox.client.filesystem;

import com.arrwhi.pbox.json.MetaData;
import com.arrwhi.pbox.msg.DeleteFileMessage;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.msg.TransportFileMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

import static com.arrwhi.pbox.client.filesystem.FileSystemUtils.getRelativePath;
import static com.arrwhi.pbox.client.filesystem.FileSystemUtils.readBytes;
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
        return adapt(ev.path, ev.kind);
    }

    public Message adapt(Path p,  WatchEvent.Kind<?> kind) {
        if(kind == ENTRY_CREATE) {
            return handleCreateEvent(p.toFile());
        } else if(kind == ENTRY_MODIFY) {
            return handleModifyEvent(p.toFile());
        } else if(kind == ENTRY_DELETE) {
            return handleDeleteEvent(p.toFile());
        }

        return null;
    }

    // TODO: The following methods should probably move to a factory somewhere.

    // 0 length payload signifies create a directory.
    private Message handleCreateEvent(File f) {
        MetaData metadata = new MetaData();
        String relative = getRelativePath(rootDir, f.toString());
        metadata.setTo(relative);

        byte[] payload = new byte[0];
        if(!f.isDirectory()) {
            try {
                payload = readBytes(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new TransportFileMessage(MetaData.toJsonBytes(metadata), payload);
    }

    private Message handleDeleteEvent(File f) {
        if(f.isDirectory()) {
            return null;
        } else {
            MetaData metadata = new MetaData();
            String relative = getRelativePath(rootDir, f.toString());
            metadata.setFrom(relative);

            return new DeleteFileMessage(MetaData.toJsonBytes(metadata));
        }
    }

    private Message handleModifyEvent(File f) {
        return null;
    }
}
