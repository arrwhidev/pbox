package com.arrwhi.pbox.client.index;

import com.arrwhi.pbox.client.filesystem.FileSystemIndexer;
import com.arrwhi.pbox.util.PropertiesHelper;

/**
 * Created by arran on 30/07/17.
 */
public enum IndexService {

    INSTANCE;

    private IndexIO indexio;
    private Index index;

    IndexService() {
        this.indexio = new IndexIO(PropertiesHelper.get("indexFilePath"));
    }

    public void init() {
        // Index the file system for changes since last time.
        Index newIndex = new Index(PropertiesHelper.get("sourceDirectory"));
        FileSystemIndexer fileSystemIndexer = new FileSystemIndexer(newIndex);
        fileSystemIndexer.buildIndex();

        if(indexio.indexExists()) {
            Index oldIndex = indexio.read();
            IndexComparator indexComparator = new IndexComparator(oldIndex, newIndex);
            if (!indexComparator.areEqual()) {
                System.out.println("Num differences since last time: " + indexComparator.getDifferences().size());

                // TODO: handle differences.
                // write differences to index.
                // write differences to network.
                // write index to disk (indexio.write(newIndex);)
            } else {
                System.out.println("No differences since last time.");
            }
        } else {
            indexio.write(newIndex);
        }

        this.index = newIndex;
    }

    public Index index() {
        return index;
    }
}
