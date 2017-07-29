package com.arrwhi.pbox.client.filesystem;

import com.arrwhi.pbox.client.index.Index;
import com.arrwhi.pbox.client.index.IndexEntry;
import com.arrwhi.pbox.client.index.IndexEntryFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileSystemIndexer {

    private final Path basePath;
    private Index index;

    public FileSystemIndexer(Index index) {
        this.basePath = Paths.get(index.getRootDir());
        this.index = index;
    }

    public void buildIndex() {
        if (index.isEmpty()) {
            List<IndexEntry> initialEntries = new ArrayList<>();
            generateInitialIndex(basePath.toFile(), initialEntries);
            index.addAll(initialEntries);
        }
    }

    private List<IndexEntry> generateInitialIndex(final File folder, List<IndexEntry> entries) {
        for (final File file : folder.listFiles()) {
            if (file.isDirectory()) {
                List<IndexEntry> nestedEntries = new ArrayList<>();
                generateInitialIndex(file, nestedEntries);
                entries.add(IndexEntryFactory.create(file, nestedEntries));
            } else {
                entries.add(IndexEntryFactory.create(file));
            }
        }

        return entries;
    }

    public Index getIndex() {
        return this.index;
    }
}
