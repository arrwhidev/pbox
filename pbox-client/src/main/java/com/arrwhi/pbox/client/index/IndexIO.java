package com.arrwhi.pbox.client.index;

import com.arrwhi.pbox.util.PropertiesHelper;
import com.google.gson.Gson;
import org.apache.commons.codec.Charsets;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class responsible for reading/writing from/to the index file.
 */
public class IndexIO {

    private final String sourceDirectory;
    private final String indexFilePath;
    private Gson gson;

    public IndexIO() {
        indexFilePath = PropertiesHelper.get("indexFilePath");
        sourceDirectory = PropertiesHelper.get("sourceDirectory");
        gson = new Gson();
    }

    public boolean indexExists() {
        Path p = Paths.get(indexFilePath);
        return p.toFile().exists();
    }

    public void write(Index index) {
        String json = toJSON(index);
        FileOutputStream fos = null;
        PrintStream ps = null;

        try {
            fos = new FileOutputStream(indexFilePath);
            ps = new PrintStream(fos);
            ps.print(json);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            ps.close();

            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Index read() {
        Path p = Paths.get(indexFilePath);
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
        return gson.fromJson(json, Index.class);
    }

    String toJSON(Index index) {
        return gson.toJson(index);
    }
}
