package com.arrwhi.pbox.client.index;

import com.arrwhi.pbox.client.filesystem.FileSystemChangeEvent;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class IndexUpdater implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        FileSystemChangeEvent changeEvent = (FileSystemChangeEvent) arg;
        if (changeEvent.getKind().equals(ENTRY_CREATE)) {
            File file = changeEvent.getPath().toFile();
            IndexService.INSTANCE.add(file);
        } else if (changeEvent.getKind().equals(ENTRY_MODIFY)) {
            // TODO: Do we need to update the index if entry is modified? Could this be a rename event?
        } else if (changeEvent.getKind().equals(ENTRY_DELETE)) {
            // TODO: Do nothing, we will delete from index when we get DELETE_MSG_ACK from server as confirmation.
        }
    }
}
