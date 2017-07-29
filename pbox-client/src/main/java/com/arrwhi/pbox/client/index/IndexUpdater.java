package com.arrwhi.pbox.client.index;

import com.arrwhi.pbox.client.filesystem.FileSystemChangeEvent;
import com.arrwhi.pbox.util.PropertiesHelper;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

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
        File file = changeEvent.getEvent().path.toFile();

        try {
            IndexEntry indexEntry = IndexEntryFactory.create(file);
            index.add(indexEntry);

            // TODO - IndexIO.write!
            indexIO.write(index);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
