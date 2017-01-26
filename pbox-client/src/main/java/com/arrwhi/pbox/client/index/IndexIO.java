package com.arrwhi.pbox.client.index;

import com.arrwhi.pbox.json.JsonImpl;
import org.apache.commons.codec.Charsets;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IndexIO {

    private String indexPath;

    public IndexIO(String path) {
        indexPath = path;
    }

    public boolean indexExists() {
        Path indexFile = FileSystems.getDefault().getPath(indexPath);
        return indexFile.toFile().exists();
    }

    public void write(Index index) {
        String json = toJSON(index);
        try {
            new PrintStream(new FileOutputStream(indexPath)).print(json);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Index read() {
        Path p = Paths.get(indexPath);
        String json = null;
        try {
            json = Files.readAllLines(p, Charsets.UTF_8)
                    .stream()
                    .reduce((t, u) -> t + u)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fromJSON(json);
    }

    Index fromJSON(String json) {
        return (Index) new JsonImpl().fromJSON(json, Index.class);
    }

    String toJSON(Index index) {
        return new JsonImpl().toJSON(index);
    }
}