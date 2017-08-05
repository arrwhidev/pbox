package com.arrwhi.pbox.client.index;

import com.arrwhi.pbox.crypto.HashFactory;

import java.io.File;
import java.util.List;

/**
 * Created by arran on 29/07/17.
 */
public class IndexEntryFactory {

    private IndexEntryFactory() {}

    public static IndexEntry create(File file) {
        String name = file.getName();
        String path = file.getAbsolutePath();
        String hashString = HashFactory.create(file);
        return new IndexEntry(name, path, hashString);
    }

    public static IndexEntry create(File file, List<IndexEntry> entries) {
        IndexEntry entry = create(file);
        entry.setEntries(entries);
        entry.setIsDirectory(true);
        return entry;
    }
}
