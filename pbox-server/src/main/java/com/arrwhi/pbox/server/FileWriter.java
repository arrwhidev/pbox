package com.arrwhi.pbox.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileWriter {
    
    private String rootDirectory;
    
    public FileWriter(String dir) {
        this.rootDirectory = dir;
    }
    
    public void write(String path, byte[] data) {
        Path newFile = Paths.get(rootDirectory, path);
        createDirectories(newFile);
        try {
            Files.write(newFile, data);
        } catch (IOException e) {
            System.err.println("Error when writing: " + path + " - " + e.getMessage());
        }
    }

    public void createDir(String path) {
        Path newFile = Paths.get(rootDirectory, path);
        try {
            Files.createDirectory(newFile);
        } catch (IOException e) {
            System.err.println("Error when creating dir: " + path + " - " + e.getMessage());
        }
    }

    public void delete(String path) {
        Path p = Paths.get(rootDirectory, path);
        try {
            Files.delete(p);
        } catch (IOException e) {
            System.err.println("Error when deleting: " + path + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createDirectories(Path path) {
        path.toFile().getParentFile().mkdirs();
    }
}
