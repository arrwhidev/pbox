package com.arrwhi.pbox.client.index;

import com.arrwhi.pbox.client.filesystem.FileSystemChangeEvent;
import com.arrwhi.pbox.util.PropertiesHelper;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Created by arran on 17/01/2017.
 */
public class IndexUpdater implements Observer {

    private IndexIO indexIO;
    private Index index;

    public IndexUpdater(Index index) {
        this.index = index;
        this.indexIO = new IndexIO(PropertiesHelper.get("indexFilePath"));
    }

    @Override
    public void update(Observable o, Object arg) {
        FileSystemChangeEvent changeEvent = (FileSystemChangeEvent) arg;
        if (changeEvent.getKind().equals(ENTRY_CREATE)) {
            File file = changeEvent.getPath().toFile();

            try {
                IndexEntry indexEntry = IndexEntryFactory.create(file);
                index.add(indexEntry);
                indexIO.write(index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (changeEvent.getKind().equals(ENTRY_MODIFY)) {
            // TODO: Do we need to update the index if entry is modified? Could this be a rename event?
        } else if (changeEvent.getKind().equals(ENTRY_DELETE)) {
            // TODO: Remove from index if deleted.
        }
    }
}
