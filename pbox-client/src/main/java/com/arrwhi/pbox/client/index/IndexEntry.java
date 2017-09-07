package com.arrwhi.pbox.client.index;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class IndexEntry {

    private String name;
    private String filePath;
    private String hash;
    private boolean isDirectory;
    private boolean isSynced;
    private boolean isDeleted;
    private List<IndexEntry> entries;

    public IndexEntry(String name, String filePath, String hash) {
        this.name = name;
        this.filePath = filePath;
        this.hash = hash;
        this.isDirectory = false;
        this.isSynced = false;
        this.isDeleted = false;
    }

    public File getAsFile() {
        return this.getAsPath().toFile();
    }

    public Path getAsPath() {
        return Paths.get(this.getFilePath());
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getHash() {
        return hash;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    void setIsDirectory(boolean directory) {
        isDirectory = directory;
    }

    public List<IndexEntry> getEntries() {
        return entries;
    }

    void setEntries(List<IndexEntry> entries) {
        this.entries = entries;
    }

    public boolean isSynced() {
        return isSynced;
    }

    void setSynced(boolean synced) {
        isSynced = synced;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        String fileOrDir = isDirectory ? "[D]" : "[F]";
        return String.format("%s %s:%s", fileOrDir, filePath, hash);
    }

    @Override
    public boolean equals(Object o) {
        IndexEntry e = (IndexEntry) o;
        return
            this.name.equals(e.getName()) &&
            this.filePath.equals(e.getFilePath()) &&
            this.hash.equals(e.getHash());
    }

    // TODO: hashcode
}
