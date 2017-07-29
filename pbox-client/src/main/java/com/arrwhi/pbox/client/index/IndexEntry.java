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
    private List<IndexEntry> entries;
    
    public IndexEntry(String name, String filePath, String hash) {
        this.name = name;
        this.filePath = filePath;
        this.hash = hash;
        this.isDirectory = false;
        this.isSynced = false;
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

    public void setIsDirectory(boolean directory) {
        isDirectory = directory;
    }

    public List<IndexEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<IndexEntry> entries) {
        this.entries = entries;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    @Override
    public String toString() {
        String fileOrDir = isDirectory ? "[D]" : "[F]";
        return String.format("%s %s:%s", fileOrDir, filePath, hash);
    }

    public boolean equals(IndexEntry e) {
        return
            this.name.equals(e.getName()) &&
            this.filePath.equals(e.getFilePath()) &&
            this.hash.equals(e.getHash());
    }

    // TODO - hashCode()
}
