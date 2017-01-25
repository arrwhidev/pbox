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
    private List<IndexEntry> entries;
    
    public IndexEntry(String name, String filePath, String hash) {
        this.name = name;
        this.filePath = filePath;
        this.hash = hash;
        this.isDirectory = false;
    }

    public IndexEntry(String name, String filePath, String hash, List<IndexEntry> entries) {
        this.name = name;
        this.filePath = filePath;
        this.hash = hash;
        this.isDirectory = true;
        this.entries = entries;
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

    public void setName(String name) {
        this.name = name;
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

    public List<IndexEntry> getEntries() {
        return entries;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public void setEntries(List<IndexEntry> entries) {
        this.entries = entries;
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
