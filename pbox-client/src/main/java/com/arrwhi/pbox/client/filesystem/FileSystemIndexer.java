package com.arrwhi.pbox.client.filesystem;

import com.arrwhi.pbox.client.index.Index;
import com.arrwhi.pbox.client.index.IndexEntry;
import com.arrwhi.pbox.client.io.MessageWriter;
import com.arrwhi.pbox.msg.Message;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static com.arrwhi.pbox.client.filesystem.FileSystemUtils.*;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class FileSystemIndexer extends Observable {

    private static final String DIR_HASH = "";

    private final Path basePath;
    private Index index;
    private FileSystemEventToMessageAdapter adapter;
    private MessageWriter msgWriter;

    public FileSystemIndexer(Index index, FileSystemEventToMessageAdapter adapter, MessageWriter msgWriter) {
        this.basePath = Paths.get(index.getRootDir());
        this.adapter = adapter;
        this.index = index;
        this.msgWriter = msgWriter;
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

            Message msg = this.adapter.adapt(file.toPath(), ENTRY_CREATE);
            if (msg != null) {
                msgWriter.writeMessage(msg);
            }
        }
        return entries;
    }


    public Index getIndex() {
        return this.index;
    }
}
