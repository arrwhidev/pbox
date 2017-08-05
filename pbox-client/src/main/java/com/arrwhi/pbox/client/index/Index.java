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

    public IndexEntry getByHash(String hash) throws IndexEntryNotFoundException {
        IndexEntry ie = getByHash(hash, entries);
        if (ie == null) {
            throw new IndexEntryNotFoundException(hash);
        }

        return ie;
    }

    private IndexEntry getByHash(String hash, List<IndexEntry> entries) {
        for (IndexEntry ie : entries) {
            if (ie.getHash().equals(hash)) {
                return ie;
            }

            if (ie.getEntries() != null) {
                IndexEntry nestedIe = getByHash(hash, ie.getEntries());
                if (nestedIe != null) {
                    return nestedIe;
                }
            }
        }
        return null;
    }

    public void add(IndexEntry indexEntry) {
        entries.add(indexEntry);
    }

    public void addAll(List<IndexEntry> initialEntries) {
        entries.addAll(initialEntries);
    }

    public boolean containsEntry(IndexEntry entry) {
        return containsEntry(entry, this.entries);
    }

    private boolean containsEntry(IndexEntry entry, List<IndexEntry> entries) {
        boolean containsEntry = false;
        for(IndexEntry ie : entries) {
            if (entry.equals(ie)) {
                containsEntry = true;
                break;
            }

            if (ie.isDirectory() && containsEntry(entry, ie.getEntries())) {
                containsEntry = true;
                break;
            }
        }

        return containsEntry;
    }
}