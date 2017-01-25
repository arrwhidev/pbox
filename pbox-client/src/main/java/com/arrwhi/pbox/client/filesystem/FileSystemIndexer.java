package com.arrwhi.pbox.client.filesystem;

import com.arrwhi.pbox.client.index.Index;
import com.arrwhi.pbox.client.index.IndexEntry;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.arrwhi.pbox.client.filesystem.FileSystemUtils.*;

public class FileSystemIndexer {

    private static final String DIR_HASH = "";

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
            String name = file.getName();
            String path = file.getAbsolutePath();

            // TODO - FileSystemUtils.createIndexEntry for when isDirectory

            if (file.isDirectory()) {
                List<IndexEntry> nestedEntries = new ArrayList<>();
                generateInitialIndex(file, nestedEntries);
                entries.add(new IndexEntry(name, path, DIR_HASH, nestedEntries));
            } else {
                try {
                    entries.add(createIndexEntry(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return entries;
    }

    public Index getIndex() {
        return this.index;
    }
}
