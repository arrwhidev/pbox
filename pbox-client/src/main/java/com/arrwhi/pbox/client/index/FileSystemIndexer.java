package com.arrwhi.pbox.client.index;

import com.arrwhi.pbox.util.PropertiesHelper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileSystemIndexer {

    private static final String SOURCE_DIR = PropertiesHelper.get("sourceDirectory");

    private FileSystemIndexer() {}

    public static Index buildIndex(String path) {
        Index index = new Index(path);
        Path basePath = Paths.get(path);

        if (index.isEmpty()) {
            List<IndexEntry> initialEntries = new ArrayList<>();
            generateInitialIndex(basePath.toFile(), initialEntries);
            index.addAll(initialEntries);
        }

        return index;
    }

    public static Index buildIndex() {
        return buildIndex(SOURCE_DIR);
    }

    private static List<IndexEntry> generateInitialIndex(final File folder, List<IndexEntry> entries) {
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
}