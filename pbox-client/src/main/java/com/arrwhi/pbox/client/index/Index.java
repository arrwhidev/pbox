package com.arrwhi.pbox.client.index;

import java.util.ArrayList;
import java.util.List;

public class Index {

    private String rootDir;
    private List<IndexEntry> entries;

    public Index(String rootDir) {
        this(rootDir, new ArrayList<>());
    }

    public Index(String rootDir, List<IndexEntry> entries) {
        this.rootDir = rootDir;
        this.entries = entries;
    }

    public String getRootDir() {
        return rootDir;
    }

    public List<IndexEntry> getEntries() {
        return entries;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void add(IndexEntry indexEntry) {
        entries.add(indexEntry);
    }

    public void addAll(List<IndexEntry> initialEntries) {
        entries.addAll(initialEntries);
    }

    public boolean containsEntry(IndexEntry entry) {
        boolean containsEntry = false;
        for(IndexEntry ie : entries) {
            if (entry.equals(ie)) {
                containsEntry = true;
                break;
            }
        }

        return containsEntry;
    }
}